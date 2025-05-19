package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validator.NotBlankCouldBeNull;
import ru.practicum.shareit.validator.marker.Create;
import ru.practicum.shareit.validator.marker.Update;

@Data
public class UserDto {

    @NotBlankCouldBeNull(groups = Update.class)
    @NotBlank(groups = Create.class)
    private String name;

    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = Create.class)
    private String email;
}
