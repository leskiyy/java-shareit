package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validator.NotBlankCouldBeNull;
import ru.practicum.shareit.validator.marker.Create;
import ru.practicum.shareit.validator.marker.Update;

@Data
public class ItemDto {

    @NotBlank(groups = Create.class)
    @NotBlankCouldBeNull(groups = Update.class)
    private String name;

    @NotBlank(groups = Create.class)
    @NotBlankCouldBeNull(groups = Update.class)
    private String description;

    @NotNull(groups = {Create.class, Update.class})
    private Boolean available;

    private Long requestId;
}
