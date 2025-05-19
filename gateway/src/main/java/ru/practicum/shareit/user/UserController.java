package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.marker.Create;
import ru.practicum.shareit.validator.marker.Update;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserClient client;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getByUserId(@PathVariable @Positive long id) {
        log.info("Getting user id={}", id);
        return client.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(Create.class) UserDto user) {
        log.info("Saving user {}", user);
        return client.saveUser(user);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserDto user,
                                             @PathVariable @Positive long id) {
        log.info("Updating user {}", user);
        return client.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @Positive long id) {
        log.info("Deleting user with id {}", id);
        return client.deleteUserById(id);
    }
}
