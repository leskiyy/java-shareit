package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = BookingMapper.class)
public abstract class ItemMapper {

    @Autowired
    protected BookingMapper bookingMapper;

    abstract ItemDto toItemDto(Item item);

    @Mapping(target = "lastBooking", expression = "java(this.getLastBooking(item))")
    @Mapping(target = "nextBooking", expression = "java(this.getNextBooking(item))")
    @Mapping(target = "id", source = "item.id")
    abstract ItemDto toItemDtoForOwner(Item item);

    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "itemRequest", source = "dto", qualifiedByName = "itemRequestStrategy")
    abstract Item toItem(ItemCreateDto dto, Long ownerId);

    @Mapping(ignore = true, target = "id")
    abstract Comment toComment(CommentDto dto, Item item, User author);

    @Mapping(target = "authorName", source = "author.name")
    abstract CommentDto toCommentDto(Comment comment);

    abstract List<CommentDto> toCommentDtos(List<Comment> comments);

    @Named("itemRequestStrategy")
    protected ItemRequest toItemRequest(ItemCreateDto dto) {
        if (dto == null || dto.getRequestId() == null) {
            return null;
        } else {
            ItemRequest request = new ItemRequest();
            request.setId(dto.getRequestId());
            return request;
        }

    }

    protected BookingDto getLastBooking(Item item) {
        List<Booking> bookings = item.getBookings();
        if (bookings.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getStart().isAfter(now)) {
                if (i == 0) return null;
                else return bookingMapper.toBookingDto(bookings.get(i - 1));
            }
        }
        return bookingMapper.toBookingDtoForItem(bookings.getLast());
    }

    protected BookingDto getNextBooking(Item item) {
        List<Booking> bookings = item.getBookings();
        if (bookings.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(now)) {
                return bookingMapper.toBookingDtoForItem(booking);
            }
        }
        return null;
    }
}
