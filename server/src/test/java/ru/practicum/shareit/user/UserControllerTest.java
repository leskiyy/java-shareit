package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;

    @MockBean
    private final UserService service;

    @Test
    @SneakyThrows
    void getAllUsers() {
        List<UserDto> users = List.of(UserDto.builder()
                .name("name")
                .email("email")
                .id(1L)
                .build());
        when(service.getAllUsers()).thenReturn(users);

        String contentAsString = mvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(users));
    }

    @Test
    @SneakyThrows
    void getByUserId() {
        long userId = 1L;
        UserDto expected = UserDto.builder()
                .name("name")
                .email("email")
                .id(1L)
                .build();
        when(service.getUserById(userId)).thenReturn(expected);

        String contentAsString = mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void createUser() {
        UserDto toSave = UserDto.builder()
                .name("name")
                .email("email")
                .build();
        UserDto expected = UserDto.builder()
                .name("name")
                .email("email")
                .id(1L)
                .build();
        when(service.saveUser(toSave)).thenReturn(expected);

        String contentAsString = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave)))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        long itemId = 1L;
        UserDto toUpdate = UserDto.builder()
                .name("name")
                .email("email")
                .build();
        UserDto expected = UserDto.builder()
                .name("updated")
                .email("updated")
                .id(itemId)
                .build();

        when(service.updateUser(toUpdate, 1L)).thenReturn(expected);

        String contentAsString = mvc.perform(patch("/users/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    @SneakyThrows
    void deleteUserById() {
        long itemId = 1L;

        mvc.perform(delete("/users/{itemId}", itemId))
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1)).deleteUserById(itemId);
    }

    @Test
    @SneakyThrows
    void testErrorHandler_ValidationException() {
        long itemId = 1L;
        UserDto toUpdate = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        when(service.updateUser(toUpdate, 1L)).thenThrow(new ValidationException("test"));

        String contentAsString = mvc.perform(patch("/users/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(contentAsString);

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(Map.of("error", "test")));
    }

    @Test
    @SneakyThrows
    void testErrorHandler_NotFoundException() {
        long itemId = 1L;
        UserDto toUpdate = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        when(service.updateUser(toUpdate, 1L)).thenThrow(new NotFoundException("test"));

        String contentAsString = mvc.perform(patch("/users/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(contentAsString);

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(Map.of("error", "test")));
    }

    @Test
    @SneakyThrows
    void testErrorHandler_EmailConflictException() {
        long itemId = 1L;
        UserDto toUpdate = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        when(service.updateUser(toUpdate, 1L)).thenThrow(new EmailConflictException("test"));

        String contentAsString = mvc.perform(patch("/users/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(contentAsString);

        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(Map.of("error", "test")));
    }
}