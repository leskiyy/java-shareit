package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.marker.Create;
import ru.practicum.shareit.validator.marker.Update;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(Create.class) ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating item {}", itemDto);
        return client.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Update.class) ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @PathVariable @Positive long itemId) {
        log.info("Updating item {}", itemDto);
        return client.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive long itemId,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Getting item id={}", itemId);
        return client.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Getting items user id={}", userId);
        return client.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestParam(required = false) String text,
                                                   @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Searching items by text={}", text);
        return client.getItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentDto dto,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                              @PathVariable @Positive long itemId) {
        log.info("Posting comment {} for item id{} by user id{}", dto, itemId, userId);
        return client.postComment(userId, dto, itemId);
    }
}
