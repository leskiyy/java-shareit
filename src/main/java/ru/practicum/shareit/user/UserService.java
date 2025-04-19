package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("There is no user with id=" + id));
        return mapToUserDto(user);
    }

    public UserDto saveUser(UserDto user) {
        User saved = repository.save(mapToUser(user));
        return mapToUserDto(saved);
    }

    public UserDto updateUser(UserDto user) {
        User updated = repository.update(mapToUser(user));
        return mapToUserDto(updated);
    }

    public void deleteUserById(long id) {
        repository.deleteById(id);
    }
}
