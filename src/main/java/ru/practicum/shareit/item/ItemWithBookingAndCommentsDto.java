package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.LastBookingDto;
import ru.practicum.shareit.booking.NextBookingDto;

import java.util.ArrayList;
import java.util.Collection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemWithBookingAndCommentsDto extends ItemDto {

    private LastBookingDto lastBooking;

    private NextBookingDto nextBooking;

    private Collection<CommentDto> comments = new ArrayList<>();
}
