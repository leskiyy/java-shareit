package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
