package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody @Valid ItemCreateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Valid ItemUpdateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        return service.update(itemDto, userId, itemId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam(required = false) String text,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.searchByText(text, userId);
    }

}
