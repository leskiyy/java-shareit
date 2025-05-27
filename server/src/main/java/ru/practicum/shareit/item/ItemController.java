package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemCreateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto created = service.createItem(itemDto, userId);
        log.info("Successfully created item {}", created);
        return created;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemCreateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        ItemDto updated = service.update(itemDto, userId, itemId);
        log.info("Successfully update item {}", updated);
        return updated;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto item = service.getItemById(id, userId);
        log.info("Successfully get item {}", item);
        return item;
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> items = service.getItemByUserId(userId);
        log.info("Successfully get {} items", items.size());
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam(required = false) String text,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> found = service.searchByText(text, userId);
        log.info("Successfully found {} items", found.size());
        return found;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestBody CommentDto dto,
                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId) {
        CommentDto comment = service.createComment(dto, userId, itemId);
        log.info("Successfully post comment {}", comment);
        return comment;
    }

}
