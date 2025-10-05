package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT new ru.practicum.shareit.item.CommentDto(c.id, c.text, c.user.name, c.date) " +
            "FROM Comment c WHERE c.item.id = :itemId")
    Collection<CommentDto> findCommentByItemId(@Param("itemId") Long itemId);

    @Query("SELECT new ru.practicum.shareit.item.CommentDto(c.id, c.text, c.user.name, c.date) " +
            "FROM Comment c WHERE c.item.id IN :itemIds " +
            "ORDER BY c.date DESC")
    Collection<CommentDto> findCommentsByItemId(@Param("itemIds") List<Long> itemIds);
}
