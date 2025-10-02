package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto commentToCommentDto(Comment comment);
}
