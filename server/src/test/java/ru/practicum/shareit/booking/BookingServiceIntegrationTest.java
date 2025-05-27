package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.DBIntegrationTestBase;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest extends DBIntegrationTestBase {

    private final BookingService service;
    private final EntityManager entityManager;

    @Test
    void createBooking_whenBookerIsOwner() {
        long itemId = 1L;
        long bookerId = 1L;
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();
        assertThatThrownBy(() -> service.createBooking(dto, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Can't book your own item");
    }

    @Test
    void createBooking_whenItemIsNotAvailable() {
        long itemId = 4L;
        long bookerId = 1L;
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .build();
        assertThatThrownBy(() -> service.createBooking(dto, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Item is not available");
    }

    @Test
    void createBooking_whenIsCrossingOtherBookingsFirstCase() {
        long itemId = 1L;
        long bookerId = 3L;
        LocalDateTime startCrossing = LocalDateTime.of(2035, 5, 26, 19, 0);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(startCrossing.minusMinutes(1))
                .end(startCrossing.plusMinutes(1))
                .build();
        assertThatThrownBy(() -> service.createBooking(dto, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("item is already booked for this time");
    }

    @Test
    void createBooking_whenIsCrossingOtherBookingsSecondCase() {
        long itemId = 1L;
        long bookerId = 3L;
        LocalDateTime startCrossing = LocalDateTime.of(2035, 5, 26, 19, 0);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(startCrossing.plusMinutes(1))
                .end(startCrossing.plusMinutes(2))
                .build();
        assertThatThrownBy(() -> service.createBooking(dto, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("item is already booked for this time");
    }

    @Test
    void createBooking_whenIsCrossingOtherBookingsThird() {
        long itemId = 1L;
        long bookerId = 3L;
        LocalDateTime startCrossing = LocalDateTime.of(2035, 5, 26, 19, 0);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(startCrossing.plusMinutes(1))
                .end(startCrossing.plusDays(2))
                .build();
        assertThatThrownBy(() -> service.createBooking(dto, bookerId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("item is already booked for this time");
    }

    @Test
    void createBooking_whenOk() {
        long itemId = 1L;
        long bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        BookingDto booking = service.createBooking(dto, bookerId);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.id = :id",
                Booking.class);
        Booking saved = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(saved).hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end);
        assertThat(saved.getItem())
                .hasFieldOrPropertyWithValue("id", itemId);
        assertThat(saved.getBooker())
                .hasFieldOrPropertyWithValue("id", bookerId);
    }

    @Test
    void approveBooking_whenUserIsNotOwner() {
        long bookingId = 1L;
        long ownerId = 2L;

        assertThatThrownBy(() -> service.approveBooking(ownerId, bookingId, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User don't own item to approve booking");
    }

    @Test
    void approveBooking_whenBookingIsAlreadyApproved() {
        long bookingId = 2L;
        long ownerId = 1L;

        assertThatThrownBy(() -> service.approveBooking(ownerId, bookingId, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Booking is already approved");
    }

    @Test
    void approveBooking_whenBookingIsAlreadyRejected() {
        long bookingId = 5L;
        long ownerId = 3L;

        assertThatThrownBy(() -> service.approveBooking(ownerId, bookingId, false))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Booking is already rejected");
    }

    @Test
    void approveBooking_whenApproved() {
        long bookingId = 1L;
        long ownerId = 1L;

        service.approveBooking(ownerId, bookingId, true);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where id = :id",
                Booking.class);
        Booking approved = query.setParameter("id", bookingId).getSingleResult();

        assertThat(approved).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenRejected() {
        long bookingId = 1L;
        long ownerId = 1L;

        service.approveBooking(ownerId, bookingId, false);

        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where id = :id",
                Booking.class);
        Booking approved = query.setParameter("id", bookingId).getSingleResult();

        assertThat(approved).hasFieldOrPropertyWithValue("status", BookingStatus.REJECTED);
    }

    @Test
    void getBooking_whenUserIsNotOwnerAndIsNotBooker() {
        long userId = 2L;
        long bookingId = 1L;

        assertThatThrownBy(() -> service.getBooking(userId, bookingId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Can't get booking by user id=" + userId);
    }

    @Test
    void getBooking_whenUserIsOwner() {
        long userId = 1L;
        long bookingId = 1L;

        BookingDto booking = service.getBooking(userId, bookingId);

        assertThat(booking).hasFieldOrPropertyWithValue("start", LocalDateTime.parse("2021-05-26T19:00"))
                .hasFieldOrPropertyWithValue("end", LocalDateTime.parse("2021-05-27T19:00"));
    }

    @Test
    void getBooking_whenUserIsBooker() {
        long userId = 3L;
        long bookingId = 1L;

        BookingDto booking = service.getBooking(userId, bookingId);

        assertThat(booking).hasFieldOrPropertyWithValue("start", LocalDateTime.parse("2021-05-26T19:00"))
                .hasFieldOrPropertyWithValue("end", LocalDateTime.parse("2021-05-27T19:00"));
    }

    @Test
    void getBookingsByBookerId_whenStateAll() {
        long bookerId = 1L;
        State state = State.ALL;

        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(2);

        bookerId = 2L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(2);

        bookerId = 3L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);
    }

    @Test
    void getBookingsByBookerId_whenStatePast() {
        long bookerId = 1L;
        State state = State.PAST;

        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(2);

        bookerId = 2L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);

        bookerId = 3L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);
    }

    @Test
    void getBookingsByBookerId_whenStateFuture() {
        long bookerId = 1L;
        State state = State.FUTURE;

        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);

        bookerId = 2L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);

        bookerId = 3L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);
    }

    @Test
    void getBookingsByBookerId_whenStateWaiting() {
        long bookerId = 1L;
        State state = State.WAITING;

        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);

        bookerId = 2L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);

        bookerId = 3L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);
    }

    @Test
    void getBookingsByBookerId_whenStateRejected() {
        long bookerId = 1L;
        State state = State.REJECTED;

        List<BookingDto> bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(1);

        bookerId = 2L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);

        bookerId = 3L;
        bookingsByBookerId = service.getBookingsByBookerId(bookerId, state);
        assertThat(bookingsByBookerId).hasSize(0);
    }

    @Test
    void getBookingsByItemOwnerId_whenUserDontHaveAbyItems() {
        long ownerId = 4L;

        assertThatThrownBy(() -> service.getBookingsByItemOwnerId(ownerId, State.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User don't have any items");
    }

    @Test
    void getBookingsByItemOwnerId_whenOk() {
        long ownerId = 1L;

        List<BookingDto> bookingsByItemOwnerId = service.getBookingsByItemOwnerId(ownerId, State.ALL);
        assertThat(bookingsByItemOwnerId).hasSize(3);

        ownerId = 2L;
        bookingsByItemOwnerId = service.getBookingsByItemOwnerId(ownerId, State.ALL);
        assertThat(bookingsByItemOwnerId).hasSize(0);

        ownerId = 3L;
        bookingsByItemOwnerId = service.getBookingsByItemOwnerId(ownerId, State.ALL);
        assertThat(bookingsByItemOwnerId).hasSize(2);
    }
}