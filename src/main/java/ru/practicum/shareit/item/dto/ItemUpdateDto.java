package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.validator.NotBlankCouldBeNull;

@Data
@Accessors(chain = true)
public class ItemUpdateDto {
    @NotBlankCouldBeNull
    private String name;
    @NotBlankCouldBeNull
    private String description;
    private Boolean available;
}
