package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ItemDto createItem(ItemCreateDto itemDto, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        Item saved = itemRepository.save(mapper.toItem(itemDto, userId));
        return mapper.toItemDto(saved);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ItemDto update(ItemCreateDto itemDto, long userId, long itemId) {
        Item toUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("There is no item with id=" + itemId));
        if (toUpdate.getOwner().getId() != userId) {
            throw new ValidationException("Can't change item's owner");
        }
        updateNotNullFields(itemDto, toUpdate);
        Item saved = itemRepository.save(toUpdate);
        return mapper.toItemDto(saved);
    }

    @Transactional(readOnly = true)
    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no item with id=" + id));

        if (item.getOwner().getId() == userId) {
            return mapper.toItemDtoForOwner(item);
        } else {
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("There is no user with id=" + userId);
            }
            return mapper.toItemDto(item);
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getItemByUserId(long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(mapper::toItemDtoForOwner)
                .toList();
    }

    public List<ItemDto> searchByText(String text, long userId) {
        return itemRepository.findByText(text).stream()
                .map(mapper::toItemDto)
                .toList();

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CommentDto createComment(CommentDto dto, long userId, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("There is no user with id=" + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("There is no item with id=" + itemId));

        List<Booking> byBookerIdAndItemId = bookingRepository.findByBookerIdAndItemId(userId, itemId);

        boolean hasPastBooking = byBookerIdAndItemId.stream()
                .anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()));

        if (hasPastBooking) {
            Comment saved = commentRepository.save(mapper.toComment(dto, item, user));
            return mapper.toCommentDto(saved);
        } else {
            throw new ValidationException("User has not ever booked item");
        }
    }

    private static void updateNotNullFields(ItemCreateDto itemDto, Item toUpdate) {
        if (itemDto.getName() != null) {
            toUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            toUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            toUpdate.setAvailable(itemDto.getAvailable());
        }
    }

}

