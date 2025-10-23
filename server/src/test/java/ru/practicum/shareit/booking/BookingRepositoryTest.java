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
        owner = createUser("owner-repo-" + System.currentTimeMillis() + "@test.com", "Repo Test Owner");
        booker = createUser("booker-repo-" + System.currentTimeMillis() + "@test.com", "Repo Test Booker");
        item = createItem("Repo Test Item", "Repo Test description", owner, true);

        entityManager.clear();
    }

    @Test
    void findByIdWithItemAndOwner_WithExistingBooking_ShouldReturnBooking() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Optional<Booking> result = bookingRepository.findByIdWithItemAndOwner(booking.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getItem()).isNotNull();
        assertThat(result.get().getBooker()).isNotNull();
    }

    @Test
    void findByIdAndBooker_WithValidData_ShouldReturnBooking() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Optional<Booking> result = bookingRepository.findByIdAndBooker(booking.getId(), booker.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findByIdAndBooker_WithWrongBooker_ShouldReturnEmpty() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        User anotherUser = createUser("another-repo-" + System.currentTimeMillis() + "@test.com", "Another User");

        Optional<Booking> result = bookingRepository.findByIdAndBooker(booking.getId(), anotherUser.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdForAuthorOrOwner_WithAuthor_ShouldReturnBooking() {

        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), booker.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdForAuthorOrOwner_WithOwner_ShouldReturnBooking() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), owner.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByIdForAuthorOrOwner_WithWrongUser_ShouldReturnEmpty() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        User stranger = createUser("stranger-repo-" + System.currentTimeMillis() + "@test.com", "Stranger");

        Optional<Booking> result = bookingRepository.findByIdForAuthorOrOwner(booking.getId(), stranger.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findAllBookingByBookerId_WithBookings_ShouldReturnBookings() {

        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();

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
        Collection<BookingDto> result = bookingRepository.findAllBookingByBookerId(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllBookingByOwnerId_ShouldReturnOwnerBookings() {
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllBookingByOwnerId_WithNoBookings_ShouldReturnEmpty() {
        Collection<BookingDto> result = bookingRepository.findAllBookingByOwnerId(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByIdAndBookerId_WithExistingBooking_ShouldReturnTrue() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        boolean result = bookingRepository.existsByIdAndBookerId(booking.getId(), booker.getId());

        assertThat(result).isTrue();
    }

    @Test
    void existsByIdAndBookerId_WithNonExistingBooking_ShouldReturnFalse() {
        assertThat(bookingRepository.existsByIdAndBookerId(999999L, 999999L)).isFalse();
    }

    @Test
    void existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual_WithOverlappingBooking_ShouldReturnTrue() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        createBooking(item, booker, start, end, BookingStatus.WAITING);

        boolean result = bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(
                item.getId(),
                start.plusHours(12),
                end.minusHours(12)
        );

        assertThat(result).isTrue();
    }

    @Test
    void existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual_WithNonOverlappingBooking_ShouldReturnFalse() {
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        boolean result = bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(
                item.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );

        assertThat(result).isFalse();
    }

    @Test
    void findAllCurrentBookingByBookerId_ShouldReturnCurrentBookings() {
        Booking currentBooking = createBooking(item, booker,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);

        Collection<BookingDto> result = bookingRepository.findAllCurrentBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllPastBookingByBookerId_ShouldReturnPastBookings() {
        Booking pastBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2), // началось 2 дня назад
                LocalDateTime.now().minusDays(1), // закончилось вчера
                BookingStatus.APPROVED);

        Collection<BookingDto> result = bookingRepository.findAllPastBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllFutureBookingByBookerId_ShouldReturnFutureBookings() {
        Booking futureBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1), // начнется завтра
                LocalDateTime.now().plusDays(2), // закончится послезавтра
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllFutureBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllWaitingBookingByBookerId_ShouldReturnWaitingBookings() {
        Booking waitingBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllWaitingBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        BookingDto dto = result.iterator().next();
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void findAllRejectedBookingByBookerId_ShouldReturnRejectedBookings() {
        Booking rejectedBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED);

        Collection<BookingDto> result = bookingRepository.findAllRejectedBookingByBookerId(booker.getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        BookingDto dto = result.iterator().next();
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void findAllCurrentBookingByOwnerId_ShouldReturnCurrentBookingsForOwner() {
        Booking currentBooking = createBooking(item, booker,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED);

        Collection<BookingDto> result = bookingRepository.findAllCurrentBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllPastBookingByOwnerId_ShouldReturnPastBookingsForOwner() {
        Booking pastBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        Collection<BookingDto> result = bookingRepository.findAllPastBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllFutureBookingByOwnerId_ShouldReturnFutureBookingsForOwner() {
        Booking futureBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllFutureBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllWaitingBookingByOwnerId_ShouldReturnWaitingBookingsForOwner() {
        Booking waitingBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Collection<BookingDto> result = bookingRepository.findAllWaitingBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void findAllRejectedBookingByOwnerId_ShouldReturnRejectedBookingsForOwner() {
        Booking rejectedBooking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED);

        Collection<BookingDto> result = bookingRepository.findAllRejectedBookingByOwnerId(owner.getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithCompletedBooking_ShouldReturnTrue() {
        Booking completedBooking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        assertThat(result).isTrue();
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithNotCompletedBooking_ShouldReturnFalse() {
        createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        assertThat(result).isFalse();
    }

    @Test
    void existsCompletedBookingByUserAndItem_WithRejectedBooking_ShouldReturnFalse() {
        // Given - отклоненное бронирование
        createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.REJECTED);

        boolean result = bookingRepository.existsCompletedBookingByUserAndItem(booker.getId(), item.getId());

        assertThat(result).isFalse();
    }

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