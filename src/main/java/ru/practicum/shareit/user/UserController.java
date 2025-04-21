package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getByUserId(@PathVariable long id) {
        return service.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreateDto user) {
        return service.saveUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody @Valid UserUpdateDto user, @PathVariable long id) {
        return service.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        service.deleteUserById(id);
    }
}
