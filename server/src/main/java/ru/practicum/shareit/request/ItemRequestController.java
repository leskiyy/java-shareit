package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto dto,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {

        ItemRequestDto request = service.createRequest(dto, userId);
        log.info("Successfully create item request {}", request);
        return request;
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {

        List<ItemRequestDto> requests = service.getOwnersRequests(userId);
        log.info("Successfully get item {} request of user id={}", requests.size(), userId);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {

        ItemRequestDto request = service.getRequestById(requestId, userId);
        log.info("Successfully get request {}", request);
        return request;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {

        List<ItemRequestDto> requests = service.getAllRequests(userId);
        log.info("Successfully get all {} requests for user id={}", requests.size(), userId);
        return requests;
    }
}
