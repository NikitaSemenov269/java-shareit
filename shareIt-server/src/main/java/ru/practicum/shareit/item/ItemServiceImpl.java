package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.CommentDto;
import ru.practicum.DTO.ItemDto;
import ru.practicum.DTO.ItemRequestDto;
import ru.practicum.DTO.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.interfaces.RequestRepository;
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
    private final RequestRepository requestRepository;

    @Cacheable(value = "itemCreation", key = "{#ownerId, #itemRequestDto.name, #itemRequestDto.description, #itemRequestDto.available}")
    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }

        log.info("Попытка создания нового предмета.");

        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID: " + ownerId + " не найден"));

        Item item = itemMapper.toItem(itemRequestDto);
        item.setOwner(owner);

        if (itemRequestDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemRequestDto.getRequestId())
                    .orElseThrow(
                            () -> new NotFoundException("Запрос с ID " + itemRequestDto.getRequestId() + " не найден")
                    );
            item.setRequest(request);
        }

        log.info("Создан новый предмет.");
        return (itemMapper.toItemDto(itemRepository.save(item)));
    }

    @CacheEvict(value = {"items", "userItems", "itemSearch", "itemCreation"}, allEntries = true)
    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long ownerId, ItemRequestDto itemRequestDto) {

        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        log.info("Попытка обновления данных предмета с ID: {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id: " + itemId + " не найден.")
        );

        if (itemRequestDto.getName() != null && !itemRequestDto.getName().equals(item.getName())) {
            item.setName(itemRequestDto.getName());
        }
        if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getAvailable() != null && !itemRequestDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemRequestDto.getAvailable());
        }

        log.info("Данные предмета с ID: {} успешно обновлены", itemId);
        // Изменения сохранятся при коммите транзакции
        return itemMapper.toItemDto(item);
    }

    @CacheEvict(value = {"items", "userItems", "itemSearch", "itemCreation"}, allEntries = true)
    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        log.info("Попытка удаления предмета ID: {} пользователем с ID: {}", itemId, ownerId);

        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        itemRepository.deleteById(itemId);
        log.info("Успешное удаление предмета ID: {} пользователем с ID: {}", itemId, ownerId);
    }

    @Cacheable(value = "items", key = "#itemId")
    @Override
    public ItemWithBookingAndCommentsDto getItemById(Long itemId, Long userId) {
        log.info("Попытка получения предмета по ID: {}", itemId);

        itemValidation.existsByUserId(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + itemId + " не найден"));

        ItemWithBookingAndCommentsDto itemDto =
                itemMapper.itemDtoWithBookingAndComments(item);

        itemDto.setComments(commentRepository.findCommentByItemId(item.getId()));

        if (userId.equals(item.getOwner().getId())) {

            itemDto.setLastBooking(bookingRepository.findLastBookingDto(item.getId(), LocalDateTime.now()));

            itemDto.setNextBooking(bookingRepository.findNextBookingDto(item.getId(), LocalDateTime.now())
            );
        }

        return itemDto;
    }

    @Cacheable(value = "itemSearch", key = "#text")
    @Override
    public Collection<ItemDto> searchItemDtoByText(String text) {
        log.info("Попытка поиска доступных предметов по ключевым словам: {}", text);

        if (text == null || text.trim().isEmpty()) {
            log.info("Поиска доступных предметов не дал результата.");
            return Collections.emptyList();
        }
        return itemRepository.findAllByText(text.trim());
    }

    @Cacheable(value = "userItems", key = "#ownerId")
    @Override
    public Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Попытка поиска всех предметов пользователя с ID: {}", ownerId);

        itemValidation.existsByUserId(ownerId);

        Collection<ItemDto> resultCollection = itemRepository
                .findByOwnerId(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        if (!resultCollection.isEmpty()) {
            return resultCollection;
        } else {
            return new ArrayList<>();
        }
    }

    @CacheEvict(value = {"items", "userItems"}, allEntries = true)
    @Override
    @Transactional
    public void updateItemAvailable(Long itemId, Boolean bookingStatus) {
        log.info("Попытка обновления статуса брони предмета с ID: {}", itemId);

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

    @CacheEvict(value = {"items", "userItems"}, allEntries = true)
    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, String comment) {
        if (comment != null && !comment.isBlank()) {

            if (!bookingRepository.existsCompletedBookingByUserAndItem(userId, itemId)) {
                throw new ValidationException("Пользователь не арендовал эту вещь или аренда еще не завершена.");
            }

            Comment newComment = new Comment();

            newComment.setText(comment);
            newComment.setUser(userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("Пользователь не найден.")));
            newComment.setItem(itemRepository.findById(itemId).orElseThrow(() ->
                    new NotFoundException("Предмет не найден.")));
            newComment.setDate(LocalDateTime.now());

            return commentMapper.toCommentDto(commentRepository.save(newComment));
        } else {
            throw new ValidationException("Комментарий не может быть пустой строкой.");
        }
    }
}