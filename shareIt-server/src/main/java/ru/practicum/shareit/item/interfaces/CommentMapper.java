package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Comment;
import ru.practicum.DTO.CommentDto;
import ru.practicum.DTO.CommentRequestDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "user.name")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentRequestDto commentRequestDto);
}
