package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.validator.NotBlankCouldBeNull;

@Data
@Accessors(chain = true)
public class UserUpdateDto {
    @NotBlankCouldBeNull
    private String name;
    @Email
    private String email;
}
