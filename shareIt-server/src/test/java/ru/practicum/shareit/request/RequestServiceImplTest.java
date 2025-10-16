package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.DTO.ItemDtoForRequester;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final Long otherUserId = 2L;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@test.com");
        return user;
    }

    private Request createRequest(Long id, User requester) {
        Request request = new Request();
        request.setId(id);
        request.setDescription("Need a drill");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    private Item createItem(Long id, Request request) {
        User owner = createUser(otherUserId);
        Item item = new Item();
        item.setId(id);
        item.setName("Drill");
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    @Test
    void createRequest_ShouldCreateRequestSuccessfully() {
        RequestDto requestDto = new RequestDto("Need a drill");
        User requester = createUser(userId);
        Request request = createRequest(requestId, requester);
        ResponseRequestDto expectedDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", request.getCreated(), Collections.emptyList()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(expectedDto);

        ResponseRequestDto result = requestService.createRequest(requestDto, userId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Need a drill", result.getDescription());
        verify(requestRepository).save(any(Request.class));
        // Проверяем что сохраняемый request имеет правильные поля
        verify(requestRepository).save(argThat(savedRequest ->
                savedRequest.getDescription().equals("Need a drill") &&
                        savedRequest.getRequester().getId().equals(userId) &&
                        savedRequest.getCreated() != null
        ));
    }

    @Test
    void createRequest_WhenUserNotFound_ShouldThrowException() {
        RequestDto requestDto = new RequestDto("Need a drill");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(requestDto, userId));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems() {
        User user = createUser(userId);
        Request request = createRequest(requestId, user);
        Item item = createItem(1L, request);
        ItemDtoForRequester itemDto = new ItemDtoForRequester(1L, "Drill", otherUserId);
        ResponseRequestDto expectedDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", request.getCreated(), List.of(itemDto)
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(request)).thenReturn(List.of(item));
        when(itemMapper.toDtoForRequest(item)).thenReturn(itemDto);
        when(requestMapper.toDto(request)).thenReturn(expectedDto);

        ResponseRequestDto result = requestService.getRequestById(requestId, userId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertFalse(result.getItems().isEmpty());
        assertEquals("Drill", result.getItems().iterator().next().getName());
        verify(itemRepository).findByRequest(request);
    }

    @Test
    void getRequestById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(requestId, userId));
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_WhenRequestNotFound_ShouldThrowException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(requestId, userId));
    }

    @Test
    void getUserRequests_ShouldReturnUserRequestsWithItems() {
        User user = createUser(userId);
        Request request = createRequest(requestId, user);
        Item item = createItem(1L, request);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(List.of(request))).thenReturn(List.of(item));

        Collection<ResponseRequestDto> result = requestService.getUserRequests(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(requestRepository).findByRequesterId(userId);
        verify(itemRepository).findByRequestIn(List.of(request));
    }

    @Test
    void getUserRequests_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(userId));
        verify(requestRepository, never()).findByRequesterId(anyLong());
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn(Collections.emptyList());
        when(itemRepository.findByRequestIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        Collection<ResponseRequestDto> result = requestService.getUserRequests(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOtherUserRequests_ShouldReturnOtherUserRequests() {
        User otherUser = createUser(otherUserId);
        Request request = createRequest(requestId, otherUser);
        Item item = createItem(1L, request);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllRequestsExceptUser(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(List.of(request))).thenReturn(List.of(item));

        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(userId, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(requestRepository).findAllRequestsExceptUser(userId);
    }

    @Test
    void getOtherUserRequests_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getOtherUserRequests(userId, 0, 10));
        verify(requestRepository, never()).findAllRequestsExceptUser(anyLong());
    }

    @Test
    void getOtherUserRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllRequestsExceptUser(userId)).thenReturn(Collections.emptyList());
        when(itemRepository.findByRequestIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(userId, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ДОБАВЛЕН тест для проверки пагинации (хотя в репозитории она не реализована)
    @Test
    void getOtherUserRequests_WithPagination_ShouldCallRepository() {
        User otherUser = createUser(otherUserId);
        Request request = createRequest(requestId, otherUser);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllRequestsExceptUser(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(List.of(request))).thenReturn(Collections.emptyList());

        Collection<ResponseRequestDto> result = requestService.getOtherUserRequests(userId, 5, 20);

        assertNotNull(result);
        verify(requestRepository).findAllRequestsExceptUser(userId);
    }

    @Test
    void builderResponseRequestDtos_ShouldCorrectlyMapRequestsToDtos() {
        User user = createUser(userId);
        Request request1 = createRequest(1L, user);
        Request request2 = createRequest(2L, user);
        Item item1 = createItem(1L, request1);
        Item item2 = createItem(2L, request1);
        Item item3 = createItem(3L, request2);

        Collection<Request> requests = List.of(request1, request2);
        Collection<Item> items = List.of(item1, item2, item3);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn((List<Request>) requests);
        when(itemRepository.findByRequestIn(requests)).thenReturn((List<Item>) items);

        Collection<ResponseRequestDto> result = requestService.getUserRequests(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        Optional<ResponseRequestDto> request1Dto = result.stream()
                .filter(dto -> dto.getId().equals(1L))
                .findFirst();
        Optional<ResponseRequestDto> request2Dto = result.stream()
                .filter(dto -> dto.getId().equals(2L))
                .findFirst();

        assertTrue(request1Dto.isPresent());
        assertTrue(request2Dto.isPresent());

        assertEquals(2, request1Dto.get().getItems().size());
        assertEquals(1, request2Dto.get().getItems().size());

        assertTrue(request1Dto.get().getItems().stream()
                .anyMatch(item -> item.getId().equals(1L) || item.getId().equals(2L)));
        assertTrue(request2Dto.get().getItems().stream()
                .anyMatch(item -> item.getId().equals(3L)));

        verify(userRepository).existsById(userId);
        verify(requestRepository).findByRequesterId(userId);
        verify(itemRepository).findByRequestIn(requests);
    }


    @Test
    void getUserRequests_WithEmptyItems_ShouldReturnRequestsWithoutItems() {
        User user = createUser(userId);
        Request request = createRequest(requestId, user);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(List.of(request))).thenReturn(Collections.emptyList());

        Collection<ResponseRequestDto> result = requestService.getUserRequests(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ДОБАВЛЕН тест для проверки исключения при получении запроса по ID
    @Test
    void getRequestById_WhenUserDoesNotExist_ShouldThrowProperException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(requestId, userId));

        assertTrue(exception.getMessage().contains("Пользователь с ID: " + userId));
    }
}