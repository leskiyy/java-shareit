package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
