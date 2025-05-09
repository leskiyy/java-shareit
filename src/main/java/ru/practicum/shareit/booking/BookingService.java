package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingDto createBooking(BookingCreateDto dto, long bookerId) {
        validateBookingCreateDto(dto);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("There is no item with id=" + dto.getItemId()));
        if (item.getOwner().getId() == bookerId) {
            throw new ValidationException("Can't book your own item");
        }
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("There is no user with id=" + bookerId);
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        boolean isCrossing = bookingRepository.findByItemIdOrderByStart(item.getId()).stream()
                .anyMatch(booking -> isCrossingWithOtherBookings(booking, dto));
        if (isCrossing) {
            throw new ValidationException("item is already booked for this time");
        }
        Booking saved = bookingRepository.save(mapper.toBooking(dto, bookerId, item.getName()));
        return mapper.toBookingDto(saved);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BookingDto approveBooking(long ownerId, long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("There is no booking with id=" + bookingId));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ValidationException("User don't own item to approve booking");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return mapper.toBookingDto(saved);
    }

    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("There is no booking with id=" + bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException("Can't get booking by user" + userId);
        }
        return mapper.toBookingDto(booking);

    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByBookerId(long bookerId, State state) {
        Predicate<Booking> predicateByState = getPredicateByState(state);
        return bookingRepository.findByBookerId(bookerId)
                .stream()
                .filter(predicateByState)
                .map(mapper::toBookingDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByItemOwnerId(long ownerId, State state) {
        if (userRepository.existsById(ownerId)) {
            throw new NotFoundException("There is no user with id=" + ownerId);
        }
        if (itemRepository.findByOwnerId(ownerId).isEmpty()) {
            throw new NotFoundException("User don't have any items");
        }
        Predicate<Booking> predicateByState = getPredicateByState(state);
        return bookingRepository.findByItemOwnerId(ownerId).stream()
                .filter(predicateByState)
                .map(mapper::toBookingDto)
                .toList();
    }

    private Predicate<Booking> getPredicateByState(State state) {
        LocalDateTime current = LocalDateTime.now();
        return switch (state) {
            case ALL -> booking -> true;
            case PAST -> booking -> booking.getEnd().isBefore(current);
            case FUTURE -> booking -> booking.getStart().isAfter(current);
            case CURRENT -> booking -> booking.getStart().isBefore(current) && booking.getEnd().isAfter(current);
            case WAITING -> booking -> booking.getStatus() == BookingStatus.WAITING;
            case REJECTED -> booking -> booking.getStatus() == BookingStatus.REJECTED;
        };
    }

    private void validateBookingCreateDto(BookingCreateDto dto) {
        if (dto.getStart().isBefore(LocalDateTime.now()) ||
            dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Can't book item in past");
        }
        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new ValidationException("Start booking can't be after end booking");
        }
        if (dto.getStart().equals(dto.getEnd())) {
            throw new ValidationException("Start booking can't be equal to end booking");
        }
    }

    private boolean isCrossingWithOtherBookings(Booking booking, BookingCreateDto dto) {
        if (booking.getStart().isBefore(dto.getStart())) {
            return booking.getEnd().isAfter(dto.getStart());
        } else if (dto.getStart().isBefore(booking.getStart())) {
            return dto.getEnd().isAfter(booking.getStart());
        } else {
            return true;
        }
    }
}
