package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;

    @MockBean
    private final ItemRequestService service;

    @Test
    @SneakyThrows
    void createRequest() {
        long userId = 1L;
        ItemRequestDto income = ItemRequestDto.builder()
                .description("desc")
                .build();
        ItemRequestDto expected = ItemRequestDto.builder()
                .description("desc")
                .id(1L)
                .build();
        when(service.createRequest(income, userId)).thenReturn(expected);

        String contentAsString = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(income)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getUserRequests() {
        long userId = 1L;

        ItemRequestDto expected = ItemRequestDto.builder()
                .description("desc")
                .id(1L)
                .build();

        List<ItemRequestDto> expectedList = List.of(expected);
        when(service.getOwnersRequests(userId)).thenReturn(expectedList);

        String contentAsString = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", Long.toString(userId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expectedList));
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        long userId = 1L;
        long requestId = 1L;

        ItemRequestDto expected = ItemRequestDto.builder()
                .description("desc")
                .id(1L)
                .build();

        when(service.getRequestById(requestId, userId)).thenReturn(expected);

        String contentAsString = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", Long.toString(userId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getRequestsByUserId() {
        long userId = 1L;

        ItemRequestDto expected = ItemRequestDto.builder()
                .description("desc")
                .id(1L)
                .build();

        List<ItemRequestDto> expectedList = List.of(expected);
        when(service.getAllRequests(userId)).thenReturn(expectedList);

        String contentAsString = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", Long.toString(userId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expectedList));
    }
}