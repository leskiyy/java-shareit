package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody @Valid BookingCreateDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        BookingDto created = service.createBooking(bookingDto, bookerId);
        log.info("Successfully create booking {}", created);
        return created;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                     @PathVariable long bookingId,
                                     @RequestParam("approved") boolean isApproved) {
        BookingDto approved = service.approveBooking(ownerId, bookingId, isApproved);
        log.info("Successfully approved booking {}", approved);
        return approved;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        BookingDto booking = service.getBooking(userId, bookingId);
        log.info("Successfully get booking {}", booking);
        return booking;
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                  @RequestParam(value = "state",
                                                          required = false,
                                                          defaultValue = "ALL") State state) {
        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        log.info("Successfully get {} bookings by booker id={}", bookingsByBookerId.size(), bookerId);
        return bookingsByBookerId;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                               @RequestParam(value = "state",
                                                       required = false,
                                                       defaultValue = "ALL") State state) {
        List<BookingDto> bookingsByItemOwnerId = service.getBookingsByItemOwnerId(ownerId, state);
        log.info("Successfully get {} booking by item's owner id={}", bookingsByItemOwnerId.size(), ownerId);
        return bookingsByItemOwnerId;
    }
}
