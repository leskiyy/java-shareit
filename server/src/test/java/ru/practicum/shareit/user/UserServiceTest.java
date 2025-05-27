package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService service;

    @Mock
    UserRepository repository;

    @Mock
    UserMapper mapper;

    @Test
    void getAllUsers() {
        User expected = new User();
        UserDto expectedDto = new UserDto();
        when(repository.findAll()).thenReturn(List.of(expected));
        when(mapper.toUserDto(expected)).thenReturn(expectedDto);

        List<UserDto> users = service.getAllUsers();

        assertThat(users).hasSize(1).first().isSameAs(expectedDto);
    }

    @Test
    void getUserById_thenFound() {
        User expected = new User();
        UserDto expectedDto = new UserDto();
        when(repository.findById(1L)).thenReturn(Optional.of(expected));
        when(mapper.toUserDto(expected)).thenReturn(expectedDto);

        UserDto actual = service.getUserById(1L);

        assertThat(actual).isSameAs(expectedDto);
    }

    @Test
    void getUserById_thenNotFound() {
        User expected = new User();
        UserDto expectedDto = new UserDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getUserById(1L));

        assertThatThrownBy(() -> service.getUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("There is no user with id=1");
    }

    @Test
    void saveUser() {
        User toSave = new User();
        User expected = new User();
        UserDto incomeDto = new UserDto();
        UserDto expectedDto = new UserDto();

        when(repository.saveAndFlush(toSave)).thenReturn(expected);
        when(mapper.toUser(incomeDto)).thenReturn(toSave);
        when(mapper.toUserDto(expected)).thenReturn(expectedDto);

        UserDto saved = service.saveUser(incomeDto);

        assertThat(saved).isSameAs(expectedDto);
    }

    @Test
    void updateUser_whenFound() {
        UserDto toUpdateDto = new UserDto();
        User toUpdate = new User();
        User saved = new User();
        UserDto expectedDto = new UserDto();

        when(repository.findById(1L)).thenReturn(Optional.of(toUpdate));
        when(repository.saveAndFlush(toUpdate)).thenReturn(saved);
        when(mapper.toUserDto(saved)).thenReturn(expectedDto);

        UserDto actual = service.updateUser(toUpdateDto, 1L);

        assertThat(actual).isSameAs(expectedDto);
    }

}