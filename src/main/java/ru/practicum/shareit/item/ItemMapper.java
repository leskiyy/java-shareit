package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    Item mapToItem(ItemCreateDto dto, long userId) {
        return new Item()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setAvailable(dto.getAvailable())
                .setOwner(userId);
    }

    Item mapToItem(ItemUpdateDto dto, long userId, long itemId) {
        return new Item()
                .setId(itemId)
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setAvailable(dto.getAvailable())
                .setOwner(userId);
    }

    ItemDto mapToItemDto(Item item) {
        return new ItemDto()
                .setId(item.getId())
                .setName(item.getName())
                .setDescription(item.getDescription())
                .setAvailable(item.getAvailable());
    }
}
