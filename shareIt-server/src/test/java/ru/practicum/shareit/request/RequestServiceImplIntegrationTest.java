/*
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1, user2;
    private Request request;

    @BeforeEach
    void setUp() {
        // Очищаем в правильном порядке из-за foreign key constraints
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        user1 = createUser("user1@test.com", "User One");
        user2 = createUser("user2@test.com", "User Two");
        request = createRequest(user1, "Need item for testing");
    }

    @Test
    void createRequest_WithValidData_ShouldCreateRequest() {
        // Given
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Need laptop for work");

        // When
        ResponseRequestDto result = requestService.createRequest(requestDto, user1.getId());

        // Then
        assertNotNull(result);
        assertEquals("Need laptop for work", result.getDescription());
        assertEquals(user1.getId(), result.getRequesterId());
        assertNotNull(result.getCreated());
        assertNotNull(result.getId());
    }

    @Test
    void createRequest_WithNonExistingUser_ShouldThrowException() {
        // Given
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Test description");

        // When & Then
        assertThrows(NotFoundException.class, () ->
                requestService.createRequest(requestDto, 999L));
    }

    @Test
    void getRequestById_WithValidData_ShouldReturnRequest() {
        // When
        ResponseRequestDto result = requestService.getRequestById(request.getId(), user1.getId());

        // Then
        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals("Need item for testing", result.getDescription());
        assertEquals(user1.getId(), result.getRequesterId());
        assertNotNull(result.getItems()); // items может быть пустым, но не null
    }

    @Test
    void getRequestById_WithItems_ShouldReturnRequestWithItems() {
        // Given - создаем предмет для этого запроса
        Item item = createItem("Test Item", "Test Description", user2, true, request);

        // When
        ResponseRequestDto result = requestService.getRequestById(request.getId(), user1.getId());

        // Then
        assertNotNull(result.getItems());
        assertThat(result.getItems()).hasSize(1);
        assertEquals("Test Item", result.getItems().iterator().next().getName());
        assertEquals(user2.getId(), result.getItems().iterator().next().getOwnerId());
    }

    @Test
    void getRequestById_WithNonExistingRequest_ShouldThrowException() {
        assertThrows(NotFoundException.class, () ->
                requestService.getRequestById(999L, user1.getId()));
    }

    @Test
    void getRequestById_WithNonExistingUser_ShouldThrowException() {
        // When & Then
        assertThrows(NotFoundException.class, () ->
                requestService.getRequestById(request.getId(), 999L));
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() {
        // Given
        createRequest(user1, "Second request");

        // When
        Collection<ResponseRequestDto> result = requestService.getUserRequests(user1.getId());

        // Then
        assertThat(result).hasSize(2);
        for (ResponseRequestDto dto : result) {
            assertEquals(user1.getId(), dto.getRequesterId());
        }
    }

    @Test
    void getUserRequests_WithNoRequests_ShouldReturnEmpty() {
        // When
        Collection<ResponseRequestDto> result = requestService.getUserRequests(user2.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getUserRequests_WithNonExistingUser_ShouldThrowException() {
        // When & Then
        assertThrows(NotFoundException.class, () ->
                requestService.getUserRequests(999L));
    }

    @Test
    void getOtherUserRequests_ShouldReturnOtherUsersRequests() {
        // Given
        Request user2Request = createRequest(user2, "User2 request");

        // When
        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(user1.getId(), 0, 10);

        // Then
        assertThat(result).hasSize(1);
        assertEquals(user2.getId(), result.iterator().next().getRequesterId());
        assertNotEquals(user1.getId(), result.iterator().next().getRequesterId());
    }

    @Test
    void getOtherUserRequests_ShouldNotReturnUserOwnRequests() {
        // Given
        createRequest(user1, "User1 second request");
        createRequest(user2, "User2 request");

        // When
        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(user1.getId(), 0, 10);

        // Then
        // Должны вернуться только запросы user2, но не user1
        assertThat(result).hasSize(1);
        assertEquals(user2.getId(), result.iterator().next().getRequesterId());
    }

    @Test
    void getOtherUserRequests_WithPagination_ShouldReturnPaginatedResults() {
        // Given - создаем несколько запросов от user2
        for (int i = 0; i < 5; i++) {
            createRequest(user2, "Request " + i);
        }

        // When - запрашиваем с пагинацией
        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(user1.getId(), 0, 3);

        // Then - должен вернуть только 3 элемента
        assertThat(result).hasSize(3);
    }

    @Test
    void getOtherUserRequests_WithNonExistingUser_ShouldThrowException() {
        // When & Then
        assertThrows(NotFoundException.class, () ->
                requestService.getOtherUserRequests(999L, 0, 10));
    }

    // Вспомогательные методы

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Request createRequest(User requester, String description) {
        Request request = new Request();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    private Item createItem(String name, String description, User owner, boolean available, Request request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);
        return itemRepository.save(item);
    }
}*/
