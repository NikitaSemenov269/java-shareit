package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingValidator;
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingValidator bookingValidator;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_ItemNotAvailable_ThrowsException() {

        Long bookerId = 1L;
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookerId, requestDto);
        });

        verify(itemRepository).findById(1L);
        verify(bookingValidator).existsByUserId(bookerId);
    }

    @Test
    void createBooking_OwnerBooksOwnItem_ThrowsException() {

        Long ownerId = 1L;
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));


        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(ownerId, requestDto);
        });

        verify(bookingValidator).existsByUserId(ownerId);
    }
}