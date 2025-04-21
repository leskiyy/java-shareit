package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    UserDto mapToUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail());
    }

    User mapToUser(UserCreateDto dto) {
        return new User()
                .setName(dto.getName())
                .setEmail(dto.getEmail());
    }

    User mapToUser(UserUpdateDto dto, long id) {
        return new User()
                .setId(id)
                .setName(dto.getName())
                .setEmail(dto.getEmail());
    }
}
