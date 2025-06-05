package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private static final ItemMapper mapper = new ItemMapperImpl();

    @BeforeAll
    static void initBookerMapper() {
        mapper.bookingMapper = new BookingMapperImpl();
    }

    @Test
    void toItemRequest() {
        ItemCreateDto build = ItemCreateDto.builder()
                .requestId(1L)
                .build();
        ItemRequest itemRequest = mapper.toItemRequest(build);

        assertThat(itemRequest).isNotNull();
    }

    @Test
    void toItemRequest_itemRequestStrategy() {
        Item item = mapper.toItem(null, null);
        assertThat(item).isNull();
    }

    @Test
    void toItemDtoForOwner() {
        ItemDto itemDtoForOwner = mapper.toItemDtoForOwner(null);
        assertThat(itemDtoForOwner).isNull();
    }

    @Test
    void toItem() {
        Item item = mapper.toItem(null, null);
        assertThat(item).isNull();
    }

    @Test
    void getLastBooking() {
        Item build = Item.builder()
                .bookings(Collections.emptyList())
                .build();
        BookingDto lastBooking = mapper.getLastBooking(build);
        assertThat(lastBooking).isNull();
    }

    @Test
    void getLastBooking_notEmptyBookings_returnNotNull() {
        Item build = Item.builder()
                .bookings(List.of(Booking.builder()
                        .start(LocalDateTime.now().minusMinutes(1L))
                        .build()))
                .build();
        BookingDto lastBooking = mapper.getLastBooking(build);
        assertThat(lastBooking).isNotNull();
    }

    @Test
    void getNextBooking() {
        Item build = Item.builder()
                .bookings(Collections.emptyList())
                .build();
        BookingDto nextBooking = mapper.getNextBooking(build);
        assertThat(nextBooking).isNull();
    }

    @Test
    void getNextBooking_notEmptyBookings_returnNull() {
        Item build = Item.builder()
                .bookings(List.of(Booking.builder()
                        .start(LocalDateTime.now().minusMinutes(1L))
                        .build()))
                .build();
        BookingDto nextBooking = mapper.getNextBooking(build);
        assertThat(nextBooking).isNull();
    }
}