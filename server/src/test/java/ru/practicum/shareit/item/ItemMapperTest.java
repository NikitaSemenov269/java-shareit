package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.DTO.ItemDto;
import ru.practicum.DTO.ItemRequestDto;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toItemDto_WithValidItem_ShouldMapCorrectly() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = itemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getOwnerId()).isEqualTo(1L);
    }

    @Test
    void toItem_WithValidRequestDto_ShouldMapCorrectly() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);

        Item item = itemMapper.toItem(requestDto);

        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getAvailable()).isTrue();
    }
}