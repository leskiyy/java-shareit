package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    UserDto mapToUserDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail());
    }

    User mapToUser(UserDto dto) {
        return new User()
                .setId(dto.getId())
                .setName(dto.getName())
                .setEmail(dto.getEmail());
    }
}
