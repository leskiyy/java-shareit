package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;

    @MockBean
    private final ItemService service;

    @Test
    @SneakyThrows
    void createItem() {
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        ItemCreateDto toSave = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .build();

        when(service.createItem(toSave, 1L)).thenReturn(expected);

        String contentAsString = mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void updateItem() {
        long itemId = 1L;
        long userId = 1L;
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        ItemCreateDto toUpdate = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .build();

        when(service.update(toUpdate, userId, itemId)).thenReturn(expected);

        String contentAsString = mvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getItemById() {
        long itemId = 1L;
        long userId = 1L;
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();

        when(service.getItemById(itemId, userId)).thenReturn(expected);

        String contentAsString = mvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void getItemsByUserId() {
        long userId = 1L;
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        List<ItemDto> expectedList = List.of(expected);
        when(service.getItemByUserId(userId)).thenReturn(expectedList);

        String contentAsString = mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expectedList));
    }

    @Test
    @SneakyThrows
    void searchItemByText() {
        long userId = 1L;
        ItemDto expected = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .build();
        List<ItemDto> expectedList = List.of(expected);
        String text = "text";
        when(service.searchByText(text, userId)).thenReturn(expectedList);

        String contentAsString = mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", text)
                        .header("X-Sharer-User-Id", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expectedList));
    }

    @Test
    @SneakyThrows
    void postComment() {
        long userId = 1L;
        long itemId = 1L;
        CommentDto toSave = CommentDto.builder()
                .text("text")
                .build();
        CommentDto expected = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now())
                .build();
        when(service.createComment(toSave, userId, itemId)).thenReturn(expected);

        String contentAsString = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave))
                        .header("X-Sharer-User-Id", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }
}