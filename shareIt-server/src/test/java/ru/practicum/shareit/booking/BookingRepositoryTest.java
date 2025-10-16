package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.BookingDto;
import ru.practicum.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные с уникальными email
        owner = createUser("owner-repo-" + System.currentTimeMillis() + "@test.com", "Repo Test Owner");
        booker = createUser("booker-repo-" + System.currentTimeMillis() + "@test.com", "Repo Test Booker");
        item = createItem("Repo Test Item", "Repo Test description", owner, true);

        entityManager.clear();
    }

    @Test
    void findByIdWithItemAndOwner_WithExistingBooking_ShouldReturnBooking() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Optional<Booking> result = bookingRepository.findByIdWithItemAndOwner(booking.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getItem()).isNotNull();
        assertThat(result.get().getBooker()).isNotNull();
    }

    @Test
    void findByIdAndBooker_WithValidData_ShouldReturnBooking() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Optional<Booking> result = bookingRepository.findByIdAndBooker(booking.getId(), booker.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findByIdAndBooker_WithWrongBooker_ShouldReturnEmpty() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        User anotherUser = createUser("another-repo-" + System.currentTimeMillis() + "@test.com", "Another User");

        // When
        Optional<Booking> result = bookingRepository.findByIdAndBooker(booking.getId(), anotherUser.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByIdForAuthorOrOwner_WithAuthor_ShouldReturnBooking() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), booker.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdForAuthorOrOwner_WithOwner_ShouldReturnBooking() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), owner.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdForAuthorOrOwner_WithWrongUser_ShouldReturnEmpty() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        User stranger = createUser("stranger-repo-" + System.currentTimeMillis() + "@test.com", "Stranger");

        // When
        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), stranger.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAllBookingByBookerId_WithBookings_ShouldReturnBookings() {
        // Given
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();

        // Проверяем структуру DTO
        BookingDto dto = result.iterator().next();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getStart()).isNotNull();
        assertThat(dto.getEnd()).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem()).isNotNull();
        assertThat(dto.getBooker()).isNotNull();
    }

    @Test
    void findAllBookingByBookerId_WithNoBookings_ShouldReturnEmpty() {
        // When
        Collection<BookingDto> result = bookingRepository.findAllBookingByBookerId(999999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAllBookingByOwnerId_ShouldReturnOwnerBookings() {
        // Given
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllBookingByOwnerId_WithNoBookings_ShouldReturnEmpty() {
        // When
        Collection<BookingDto> result = bookingRepository.findAllBookingByOwnerId(999999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void existsByIdAndBookerId_WithExistingBooking_ShouldReturnTrue() {
        // Given
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        boolean result = bookingRepository.existsByIdAndBookerId(booking.getId(), booker.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByIdAndBookerId_WithNonExistingBooking_ShouldReturnFalse() {
        // When & Then
        assertThat(bookingRepository.existsByIdAndBookerId(999999L, 999999L)).isFalse();
    }

    @Test
    void existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual_WithOverlappingBooking_ShouldReturnTrue() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        createBooking(item, booker, start, end, BookingStatus.WAITING);

        // When - проверяем пересекающийся период
        boolean result = bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(
                item.getId(),
                start.plusHours(12), // внутри существующего бронирования
                end.minusHours(12)   // внутри существующего бронирования
        );

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual_WithNonOverlappingBooking_ShouldReturnFalse() {
        // Given
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When - проверяем период ПОСЛЕ существующего бронирования
        boolean result = bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(
                item.getId(),
                LocalDateTime.now().plusDays(3), // после окончания бронирования
                LocalDateTime.now().plusDays(4)  // после окончания бронирования
        );

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void findAllCurrentBookingByBookerId_ShouldReturnCurrentBookings() {
        // Given - бронирование, которое сейчас активно
        Booking currentBooking = createBooking(item, booker,
                LocalDateTime.now().minusHours(1), // началось час назад
                LocalDateTime.now().plusHours(1),  // закончится через час
                BookingStatus.APPROVED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllCurrentBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllPastBookingByBookerId_ShouldReturnPastBookings() {
        // Given - завершенное бронирование
        Booking pastBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2), // началось 2 дня назад
                LocalDateTime.now().minusDays(1), // закончилось вчера
                BookingStatus.APPROVED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllPastBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllFutureBookingByBookerId_ShouldReturnFutureBookings() {
        // Given - будущее бронирование
        Booking futureBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1), // начнется завтра
                LocalDateTime.now().plusDays(2), // закончится послезавтра
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllFutureBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllWaitingBookingByBookerId_ShouldReturnWaitingBookings() {
        // Given
        Booking waitingBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllWaitingBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        BookingDto dto = result.iterator().next();
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void findAllRejectedBookingByBookerId_ShouldReturnRejectedBookings() {
        // Given
        Booking rejectedBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllRejectedBookingByBookerId(booker.getId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        BookingDto dto = result.iterator().next();
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void findAllCurrentBookingByOwnerId_ShouldReturnCurrentBookingsForOwner() {
        // Given
        Booking currentBooking = createBooking(item, booker,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllCurrentBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllPastBookingByOwnerId_ShouldReturnPastBookingsForOwner() {
        // Given
        Booking pastBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllPastBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllFutureBookingByOwnerId_ShouldReturnFutureBookingsForOwner() {
        // Given
        Booking futureBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllFutureBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllWaitingBookingByOwnerId_ShouldReturnWaitingBookingsForOwner() {
        // Given
        Booking waitingBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        Collection<BookingDto> result = bookingRepository.findAllWaitingBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void findAllRejectedBookingByOwnerId_ShouldReturnRejectedBookingsForOwner() {
        // Given
        Booking rejectedBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED);

        // When
        Collection<BookingDto> result = bookingRepository.findAllRejectedBookingByOwnerId(owner.getId());

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithCompletedBooking_ShouldReturnTrue() {
        // Given - завершенное APPROVED бронирование
        Booking completedBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        // When
        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithNotCompletedBooking_ShouldReturnFalse() {
        // Given - будущее бронирование
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        // When
        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithRejectedBooking_ShouldReturnFalse() {
        // Given - отклоненное бронирование
        createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.REJECTED);

        // When
        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        // Then
        assertThat(result).isFalse();
    }

    // Вспомогательные методы
    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Item createItem(String name, String description, User owner, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        entityManager.persist(item);
        entityManager.flush();
        return item;
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        entityManager.persist(booking);
        entityManager.flush();
        return booking;
    }
}