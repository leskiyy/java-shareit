package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryInMemoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositoryInMemoryImpl itemRepository;
    private final UserRepositoryInMemoryImpl userRepository;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        validateUserByUserId(userId);
        Item saved = itemRepository.save(mapToItem(itemDto, userId));

        return mapToItemDto(saved);
    }

    public ItemDto update(ItemDto itemDto, long userId) {
        validateUserByUserId(userId);
        Item updated = itemRepository.update(mapToItem(itemDto, userId));
        return mapToItemDto(updated);
    }

    public ItemDto getItemById(long id, long userId) {
        validateUserByUserId(userId);
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("There is no item with id=" + id);
        }
        Item item = optionalItem.get();

        return mapToItemDto(item);
    }

    public List<ItemDto> getItemByUserId(long userId) {
        validateUserByUserId(userId);
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public List<ItemDto> searchByText(String text, long userId) {
        validateUserByUserId(userId);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

    }

    private void validateUserByUserId(long userId) {
        if (!userRepository.existById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
    }
}
