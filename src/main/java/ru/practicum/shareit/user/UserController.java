package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> users = service.getAllUsers();
        log.info("Successfully get {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getByUserId(@PathVariable long id) {
        UserDto user = service.getUserById(id);
        log.info("Successfully get user {}", user);
        return user;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreateDto user) {
        UserDto saved = service.saveUser(user);
        log.info("Successfully create user {}", saved);
        return saved;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody @Valid UserUpdateDto user, @PathVariable long id) {
        UserDto updated = service.updateUser(user, id);
        log.info("Successfully update user {}", updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        log.info("Successfully delete user with id {}", id);
        service.deleteUserById(id);
    }
}
