package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .toList();
    }

    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("There is no user with id=" + id));
        return mapper.toUserDto(user);
    }

    public UserDto saveUser(UserCreateDto user) {
        User saved = repository.save(mapper.toUser(user));
        return mapper.toUserDto(saved);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto updateUser(UserUpdateDto user, long id) {
        User toUpdate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no user with id=" + id));
        updateNotNullFields(user, toUpdate);
        repository.save(toUpdate);
        return mapper.toUserDto(toUpdate);
    }

    public void deleteUserById(long id) {
        repository.deleteById(id);
    }

    private static void updateNotNullFields(UserUpdateDto user, User toUpdate) {
        if (user.getName() != null) {
            toUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            toUpdate.setEmail(user.getEmail());
        }
    }
}
