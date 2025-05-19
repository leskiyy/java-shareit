package ru.practicum.shareit.booking;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "item", ignore = true)
    BookingDto toBookingDtoForItem(Booking booking);


    @Mapping(target = "item.id", source = "dto.itemId")
    @Mapping(target = "booker.id", source = "bookerId")
    @Mapping(target = "item.name", source = "itemName")
    @Mapping(target = "status", expression = "java( ru.practicum.shareit.booking.BookingStatus.WAITING)")
    Booking toBooking(BookingCreateDto dto, Long bookerId, String itemName);
}

