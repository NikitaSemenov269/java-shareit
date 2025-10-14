package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

import static ru.practicum.enums.BookingStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final BookingMapper mapper;

    /* @Cacheable(value = "bookingCreation", key = "{#bookerId," +
             " #bookingRequestDto.itemId," +
             " #bookingRequestDto.start," +
             " #bookingRequestDto.end}")*/
    @Override
    @Transactional
    public BookingDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto) {

        bookingValidator.existsByUserId(bookerId);

        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмета с " + bookingRequestDto.getItemId() + " не найден."));

        if (!item.getAvailable()) {
            throw new NotFoundException("Предмет недоступен для бронирования");
        }
        if (bookerId.equals(item.getOwner().getId())) {
            throw new ValidationException("Владелец не может бронировать собственные вещи.");
        }
        bookingValidator.bookingDateValidation(item.getId(), bookingRequestDto.getStart(), bookingRequestDto.getEnd());

        log.info("Попытка создания новой брони для предмета с ID: {}", item.getId());

        Booking booking = mapper.toBooking(bookingRequestDto);

        booking.setItem(item);
        booking.setBooker(userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID: " + bookerId + " не найден.")));
        bookingRepository.save(booking);

        log.info("Создана новая заявка на бронирование c ID: {} для предмета с ID: {}", booking.getId(),
                item.getId());

        return mapper.toDto(booking);
    }

    //    @CacheEvict(value = {"bookings", "userBookings", "ownerBookings"}, allEntries = true)
    @Override
    @Transactional
    public BookingDto updateAvailableStatusBooking(Long ownerId, Long id, Boolean approved) {
        log.info("Попытка обновления статуса брони с ID: {} владельцем вещи.", id);

        Booking booking = bookingRepository.findByIdWithItemAndOwner(id).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено."));

        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Пользователь не является владельцем вещи. В изменении статуса отказано.");
        }
        if (approved) {
            booking.setStatus(APPROVED);
            itemService.updateItemAvailable(booking.getItem().getId(), APPROVED.isStatus()); // false - предмет забронирован
            log.info("Бронирование подтверждено.");
        } else {
            booking.setStatus(REJECTED);
            itemService.updateItemAvailable(booking.getItem().getId(), REJECTED.isStatus()); // true - бронь отклонена
            log.info("Бронирование отклонено.");
        }
        return mapper.toDto(booking);
    }

    @Override
    @Transactional
    public void canceledBookingById(Long bookerId, Long bookingId) {
        log.info("Попытка отмены брони с ID: {} автором.", bookingId);

        bookingValidator.existsByUserId(bookerId);

        Booking booking = bookingRepository.findByIdAndBooker(bookingId, bookerId).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено."));

        if (booking.getStatus() == WAITING || booking.getStatus() == APPROVED) {
            booking.setStatus(CANCELED);
            itemService.updateItemAvailable(booking.getItem().getId(), CANCELED.isStatus()); // true
        }
        log.info("Успешное отмена брони с ID: {}", bookingId);
    }
    // для пуша
    //    @Cacheable(value = "bookings", key = "#bookingId")
    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Попытка получения информации о брони с ID: {}", bookingId);

        bookingValidator.existsByUserId(userId);

        Booking booking = bookingRepository.findByIdForAuthorOrOwner(bookingId, userId).orElseThrow(() ->
                new NotFoundException("Бронь не найдена."));

        return mapper.toDto(booking);
    }

    //    @Cacheable(value = "userBookings", key = "{#bookerId, #state}")
    @Override
    public Collection<BookingDto> getAllBookingByBookerId(Long bookerId, State state) {
        // по умолчанию state = all

        bookingValidator.existsByUserId(bookerId);

        switch (state) {
            case ALL -> {
                return bookingRepository.findAllBookingByBookerId(bookerId);
            }
            case CURRENT -> {
                return bookingRepository.findAllCurrentBookingByBookerId(bookerId);
            }
            case PAST -> {
                return bookingRepository.findAllPastBookingByBookerId(bookerId);
            }
            case FUTURE -> {
                return bookingRepository.findAllFutureBookingByBookerId(bookerId);
            }
            case WAITING -> {
                return bookingRepository.findAllWaitingBookingByBookerId(bookerId);
            }
            case REJECTED -> {
                return bookingRepository.findAllRejectedBookingByBookerId(bookerId);
            }
        }
        return new ArrayList<>();
    }

    //    @Cacheable(value = "ownerBookings", key = "{#ownerId, #state}")
    @Override
    public Collection<BookingDto> getAllBookingByOwnerId(Long ownerId, State state) {

        bookingValidator.existsByUserId(ownerId);

        switch (state) {
            case ALL -> {
                return bookingRepository.findAllBookingByOwnerId(ownerId);
            }
            case CURRENT -> {
                return bookingRepository.findAllCurrentBookingByOwnerId(ownerId);
            }
            case PAST -> {
                return bookingRepository.findAllPastBookingByOwnerId(ownerId);
            }
            case FUTURE -> {
                return bookingRepository.findAllFutureBookingByOwnerId(ownerId);
            }
            case WAITING -> {
                return bookingRepository.findAllWaitingBookingByOwnerId(ownerId);
            }
            case REJECTED -> {
                return bookingRepository.findAllRejectedBookingByOwnerId(ownerId);
            }
        }
        return new ArrayList<>();
    }
}
