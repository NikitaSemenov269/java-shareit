package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemWithBookingAndCommentsDto extends ItemDto {

    private LocalDateTime lastStart;
    private LocalDateTime lastEnd;

    private LocalDateTime nextStart;
    private LocalDateTime nextEnd;

    private Collection<CommentDto> comments = new ArrayList<>();
}
