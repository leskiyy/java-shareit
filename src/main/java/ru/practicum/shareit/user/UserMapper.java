package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User dto);

    User toUser(UserCreateDto user);
}
