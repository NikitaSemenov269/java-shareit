package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.CommentDto;
import ru.practicum.DTO.ItemDto;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class ItemRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private ItemRepository itemRepository;
    @Autowired private CommentRepository commentRepository;

    @Test
    void findAllByText_WithMatchingText_ShouldReturnItems() {
        User owner = createUser("owner-repo@test.com", "Repo Owner");
        Item item = createItem("Laptop", "Gaming laptop for tests", owner, true);

        Collection<ItemDto> result = itemRepository.findAllByText("laptop");

        assertThat(result).isNotEmpty();
    }

    @Test
    void findAllByText_WithNonMatchingText_ShouldReturnEmpty() {
        User owner = createUser("owner-repo2@test.com", "Repo Owner 2");
        createItem("Book", "Test book", owner, true);

        Collection<ItemDto> result = itemRepository.findAllByText("laptop");

        assertThat(result).isEmpty();
    }

    @Test
    void findByOwnerId_ShouldReturnOwnerItems() {
        User owner = createUser("owner-repo3@test.com", "Repo Owner 3");
        createItem("Item 1", "Description 1", owner, true);
        createItem("Item 2", "Description 2", owner, true);

        Collection<Item> result = itemRepository.findByOwnerId(owner.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findCommentByItemId_ShouldReturnComments() {
        User owner = createUser("owner-repo4@test.com", "Repo Owner 4");
        User commenter = createUser("commenter@test.com", "Commenter");
        Item item = createItem("Test Item", "Test Description", owner, true);
        createComment(item, commenter, "Great item!");

        Collection<CommentDto> result = commentRepository.findCommentByItemId(item.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.iterator().next().getText()).isEqualTo("Great item!");
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

    private Comment createComment(Item item, User user, String text) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setUser(user);
        comment.setDate(LocalDateTime.now());
        entityManager.persist(comment);
        entityManager.flush();
        return comment;
    }
}