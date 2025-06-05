package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;

    @MockBean
    private final BookingService service;

    @Test
    @SneakyThrows
    void createBooking() {
        BookingCreateDto build = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("booker")
                        .build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(service.createBooking(build, 1L)).thenReturn(booking);

        String contentAsString = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build)))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(booking));
    }

    @Test
    @SneakyThrows
    void approveBooking() {
        Long bookingId = 1L;
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("booker")
                        .build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(service.approveBooking(1L, 1L, true)).thenReturn(expected);

        String contentAsString = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getBooking() {
        Long bookingId = 1L;
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("booker")
                        .build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(service.getBooking(1L, 1L)).thenReturn(expected);

        String contentAsString = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getBookingsByBookerId() {
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("booker")
                        .build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        when(service.getBookingsByBookerId(1L, State.REJECTED)).thenReturn(List.of(expected));

        String contentAsString = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "REJECTED"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(List.of(expected)));
    }

    @Test
    @SneakyThrows
    void getBookingsByOwner() {
        long ownerId = 2L;
        BookingDto expected = BookingDto.builder()
                .id(1L)
                .item(ItemDto.builder()
                        .id(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("booker")
                        .build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.REJECTED)
                .build();

        when(service.getBookingsByItemOwnerId(1L, State.REJECTED)).thenReturn(List.of(expected));

        String contentAsString = mvc.perform(get("/bookings/owner")
                        .param("state", "REJECTED")
                        .header("X-Sharer-User-Id", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(List.of(expected)));
    }
}