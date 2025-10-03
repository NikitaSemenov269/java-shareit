package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.CommentRequestDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "userId", source = "item.id")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentRequestDto commentRequestDto);
}
