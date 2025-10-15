/*
package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class RequestRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private RequestRepository requestRepository;

    @Test
    void findByRequesterId_ShouldReturnUserRequests() {
        User user = createUser("user@test.com", "Test User");
        createRequest(user, "Request 1");
        createRequest(user, "Request 2");

        Collection<Request> result = requestRepository.findByRequesterId(user.getId());

        assertThat(result).hasSize(2);
        for (Request request : result) {
            assertThat(request.getRequester().getId()).isEqualTo(user.getId());
        }
    }

    @Test
    void findAllRequestsExceptUser_ShouldReturnOtherUsersRequests() {
        User user1 = createUser("user1@test.com", "User One");
        User user2 = createUser("user2@test.com", "User Two");
        createRequest(user1, "User1 request");
        createRequest(user2, "User2 request");

        Collection<Request> result = requestRepository.findAllRequestsExceptUser(user1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getRequester().getId()).isEqualTo(user2.getId());
    }

    @Test
    void findAllRequestsExceptUser_WithNoOtherRequests_ShouldReturnEmpty() {
        User user = createUser("user@test.com", "Test User");
        createRequest(user, "User request");

        Collection<Request> result = requestRepository.findAllRequestsExceptUser(user.getId());

        assertThat(result).isEmpty();
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Request createRequest(User requester, String description) {
        Request request = new Request();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(java.time.LocalDateTime.now());
        entityManager.persist(request);
        entityManager.flush();
        return request;
    }
}*/
