package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    Comment toComment(CommentDto commentDto);
}
