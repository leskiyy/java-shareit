package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid ItemRequestDto dto,
                                                @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating item request {}", dto);
        return client.postRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Getting user's requests, user id={}", userId);
        return client.getUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable @Positive long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive long userId) {

        log.info("Getting request id={}", requestId);
        return client.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {

        log.info("Getting all requests for user id={}", userId);
        return client.getRequestsByUserId(userId);
    }
}
