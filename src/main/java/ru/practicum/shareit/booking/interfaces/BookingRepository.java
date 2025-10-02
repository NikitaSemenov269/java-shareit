package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByIdAndBookerId(Long id, Long bookerId);

    boolean existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(Long itemId, LocalDateTime end, LocalDateTime start);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH i.owner WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithItemAndOwner(@Param("bookingId") Long bookingId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker WHERE b.id = :bookingId AND b.booker.id = :bookerId")
    Optional<Booking> findByIdAndBooker(@Param("bookingId") Long bookingId,
                                        @Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker " +
            "WHERE b.id = :bookingId AND (b.booker.id = :userId OR i.owner.id = :userId)")
    Optional<Booking> findByIdForAuthorOrOwner(@Param("bookingId") Long bookingId,
                                               @Param("userId") Long userId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllCurrentBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllPastBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllFutureBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId AND b.status = WAITING " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllWaitingBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.booker.id = :bookerId AND b.status = REJECTED " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllRejectedBookingByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    Collection<BookingDto> findAllBookingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllCurrentBookingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllPastBookingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllFutureBookingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = WAITING " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllWaitingBookingByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT NEW ru.practicum.shareit.booking.BookingDto(" +
            "b.id, b.start, b.end, b.status, b.item.id, b.booker.id) " +
            "FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = REJECTED " +
            "ORDER BY b.start DESC")
    Collection<BookingDto> findAllRejectedBookingByOwnerId(@Param("ownerId") Long ownerId);

    // Проверка существования завершенной брони для пользователя и предмета
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = APPROVED " +
            "AND b.end < CURRENT_TIMESTAMP")
    boolean existsCompletedBookingByUserAndItem(@Param("userId") Long userId,
                                                @Param("itemId") Long itemId);
}
