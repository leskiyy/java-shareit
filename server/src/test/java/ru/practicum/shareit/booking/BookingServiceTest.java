package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper mapper;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_shouldThrowWhenItemNotFound() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(dto, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("There is no item with id=");

        verify(itemRepository).findById(1L);
        verifyNoInteractions(bookingRepository, mapper);
    }

    @Test
    void createBooking_shouldThrowWhenBookerIsOwner() {
        User owner = User.builder()
                .id(2L)
                .build();
        Item item = Item.builder()
                .owner(owner)
                .build();

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(dto, 2L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Can't book your own item");
    }

    @Test
    void createBooking_shouldThrowWhenItemNotAvailable() {
        User owner = User.builder()
                .id(2L)
                .build();
        Item item = Item.builder()
                .owner(owner)
                .available(false)
                .build();
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(dto, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Item is not available");
    }

    @Test
    void createBooking_shouldThrowWhenDatesAreInvalid() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingCreateDto pastDto = new BookingCreateDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.createBooking(pastDto, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Can't book item in past");
    }

    @Test
    void approveBooking_shouldThrowWhenUserNotOwner() {
        Item item = Item.builder()
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();
        Booking booking = Booking.builder()
                .item(item)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("User don't own item to approve booking");
    }

    @Test
    void approveBooking_shouldUpdateStatusWhenApproved() {
        User owner = User.builder()
                .id(1L)
                .build();
        Item item = Item.builder()
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(mapper.toBookingDto(any())).thenReturn(new BookingDto());

        BookingDto result = bookingService.approveBooking(1L, 1L, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result).isNotNull();
        verify(bookingRepository).save(booking);
    }

    @Test
    void getBooking_shouldThrowWhenUserNotRelated() {
        Booking booking = Booking.builder()
                .booker(User.builder()
                        .id(2L)
                        .build())
                .item(Item.builder()
                        .owner(User.builder()
                                .id(3L)
                                .build())
                        .build())
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(1L, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Can't get booking by user");
    }

    @Test
    void getBookingsByItemOwnerId_shouldThrowWhenNoItems() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of());

        assertThatThrownBy(() -> bookingService.getBookingsByItemOwnerId(1L, State.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User don't have any items");
    }

}