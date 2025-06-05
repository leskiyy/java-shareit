package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(ignore = true, target = "items")
    ItemRequestDto toDto(ItemRequest request);

    ItemRequestDto toDtoWithItems(ItemRequest request);

    @Mapping(target = "author.id", source = "userId")
    ItemRequest toItemRequest(ItemRequestDto dto, Long userId);

}
