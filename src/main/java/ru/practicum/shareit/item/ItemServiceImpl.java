package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemValidation itemValidation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, Item newItem) {
        if (newItem.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }

        log.info("Попытка создания нового предмета.");

        itemValidation.itemValidationByUserId(ownerId);

        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID: " + ownerId + " не найден"));

        newItem.setOwner(owner);
        itemRepository.save(newItem);
        log.info("Создан новый предмет с ID: {}", newItem.getId());
        return itemMapper.itemToItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long ownerId, Item updateItem) {

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByUserId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        log.info("Попытка обновления данных предмета с ID: {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id: " + itemId + " не найден.")
        );

        if (updateItem.getName() != null && !updateItem.getName().equals(item.getName())) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().equals(item.getDescription())) {
            item.setDescription(updateItem.getDescription());
        }

        log.info("Данные предмета с ID: {} успешно обновлены", itemId);
        // Изменения сохранятся при коммите транзакции
        return itemMapper.itemToItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        log.info("Попытка удаления предмета ID: {} пользователем с ID: {}", itemId, ownerId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByUserId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        itemRepository.deleteById(itemId);
        log.info("Успешное удаление предмета ID: {} пользователем с ID: {}", itemId, ownerId);
    }

    @Override
    public ItemWithCommentsDto getItemById(Long itemId) {
        log.info("Попытка получения предмета по ID: {}", itemId);

        itemValidation.itemValidationById(itemId);

        ItemWithCommentsDto itemWithCommentsDto = itemMapper.itemDtoWithComments(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + itemId + " не найден")));

        itemWithCommentsDto.setComments(commentRepository.findCommentByItemId(itemId));
        return itemWithCommentsDto;
    }

    @Override
    public Collection<ItemDto> searchItemDtoByText(String text) {
        log.info("Попытка поиска доступных предметов по ключевым словам: {}", text);

        if (text == null || text.trim().isEmpty()) {
            log.info("Поиска доступных предметов не дал результата.");
            return Collections.emptyList();
        }
        return itemRepository.findAllByText(text.trim());
    }

    @Override
    public Collection<ItemWithBookingAndCommentsDto> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Попытка поиска всех предметов пользователя с ID: {}", ownerId);

        itemValidation.itemValidationByUserId(ownerId);
        itemValidation.existsByUserId(ownerId);

        Collection<ItemWithBookingAndCommentsDto> resultCollection = itemRepository
                .findByOwnerIdWithBookings(ownerId);

        if (!resultCollection.isEmpty()) {
            List<Long> itemIdcollection = resultCollection.stream()
                    .map(ItemWithBookingAndCommentsDto::getId)
                    .collect(Collectors.toList());

            Map<Long, List<CommentDto>> commentsByItemId = commentRepository
                    .findCommentsByItemId(itemIdcollection)
                    .stream()
                    .collect(Collectors.groupingBy(CommentDto::getItemId));

            resultCollection.forEach(item -> {
                List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), new ArrayList<>());
                item.setComments(comments);
            });

            return resultCollection;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void updateItemAvailable(Long itemId, Boolean bookingStatus) {
        log.info("Попытка обновления статуса брони предмета с ID: {}", itemId);

        itemValidation.itemValidationById(itemId);

        if (bookingStatus == null) {
            throw new ValidationException("Статус бронирования не может быть null");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id: " + itemId + " не найден."));

        if (!bookingStatus.equals(item.getAvailable())) {
            item.setAvailable(bookingStatus);
            itemRepository.save(item);
            log.info("Успешное обновление статуса брони предмета с ID: {}", itemId);
        }
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long itemId, String comment) {

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByUserId(userId);

        if (!bookingRepository.existsCompletedBookingByUserAndItem(userId, itemId)) {
            throw new ValidationException("Пользователь не арендовал эту вещь или аренда еще не завершена.");
        }

        Comment newComment = new Comment();
        newComment.setUser(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден.")));
        newComment.setItem(itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет не найден.")));
        newComment.setComment(comment);
        newComment.setDate(LocalDateTime.now());

        commentRepository.save(newComment);

        return commentMapper.commentToCommentDto(newComment);
    }
}