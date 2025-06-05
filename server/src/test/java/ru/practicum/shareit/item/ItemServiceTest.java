package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemService service;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemMapper mapper;

    @Test
    void createItem_whenUserExists() {
        long userId = 1L;
        ItemCreateDto dtoToSave = new ItemCreateDto();
        Item toSave = new Item();
        Item expected = new Item();
        ItemDto expectedDto = new ItemDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.save(toSave)).thenReturn(expected);
        when(mapper.toItemDto(expected)).thenReturn(expectedDto);
        when(mapper.toItem(dtoToSave, userId)).thenReturn(toSave);

        ItemDto actual = service.createItem(dtoToSave, userId);

        assertThat(actual).isSameAs(expectedDto);

        verify(userRepository, times(1)).existsById(userId);
        verify(mapper, times(1)).toItem(dtoToSave, userId);
        verify(mapper, times(1)).toItemDto(expected);
        verify(itemRepository, times(1)).save(toSave);
    }

    @Test
    void createItem_whenUserNotExists() {
        long userId = 1L;
        ItemCreateDto dtoToSave = new ItemCreateDto();
        ItemDto expectedDto = new ItemDto();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.createItem(dtoToSave, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("There is no user with id=1");

        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void update_whenItemNotFound() {
        long itemId = 1L;
        long userId = 1L;
        ItemCreateDto itemDto = new ItemCreateDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(itemDto, itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("There is no item with id=" + itemId);
    }

    @Test
    void update_whenUserIsNotOwner() {
        long itemId = 1L;
        long userId = 1L;
        ItemCreateDto itemDto = new ItemCreateDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(Item.builder()
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build()));

        assertThatThrownBy(() -> service.update(itemDto, itemId, userId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Can't change item's owner");
    }

    @Test
    void update_whenOK() {
        long itemId = 1L;
        long userId = 1L;
        ItemCreateDto itemDto = new ItemCreateDto();
        Item toUpdate = Item.builder()
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build();
        ItemDto expected = new ItemDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toUpdate));
        when(itemRepository.save(toUpdate)).thenReturn(toUpdate);
        when(mapper.toItemDto(toUpdate)).thenReturn(expected);

        ItemDto actual = service.update(itemDto, itemId, userId);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void getItemById_whenItemIsNotFound() {
        long itemId = 1L;
        long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getItemById(itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("There is no item with id=" + userId);
    }

    @Test
    void getItemById_whenUserIsOwner() {
        long itemId = 1L;
        long userId = 1L;
        Item returned = Item.builder()
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();
        ItemDto expected = new ItemDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(returned));
        when(mapper.toItemDtoForOwner(returned)).thenReturn(expected);

        ItemDto actual = service.getItemById(itemId, userId);

        assertThat(actual).isSameAs(expected);
        verify(mapper, times(1)).toItemDtoForOwner(returned);
        verify(mapper, never()).toItemDto(returned);
    }

    @Test
    void getItemById_whenUserIsNotFound() {
        long itemId = 1L;
        long userId = 1L;
        long ownerId = 2L;
        Item returned = Item.builder()
                .owner(User.builder()
                        .id(ownerId)
                        .build())
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(returned));
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.getItemById(itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("There is no user with id=" + userId);
    }

    @Test
    void getItemById_whenUserIsNotOwner() {
        long itemId = 1L;
        long userId = 1L;
        long ownerId = 2L;
        Item returned = Item.builder()
                .owner(User.builder()
                        .id(ownerId)
                        .build())
                .build();
        ItemDto expected = new ItemDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(returned));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(mapper.toItemDto(returned)).thenReturn(expected);

        ItemDto actual = service.getItemById(itemId, userId);

        assertThat(actual).isSameAs(expected);
        verify(mapper, times(1)).toItemDto(returned);
        verify(mapper, never()).toItemDtoForOwner(returned);
    }

    @Test
    void getItemByUserId() {
        long userId = 1L;
        List<Item> returned = List.of(new Item());
        ItemDto expected = new ItemDto();
        when(itemRepository.findByOwnerId(userId)).thenReturn(returned);
        when(mapper.toItemDtoForOwner(returned.getFirst())).thenReturn(expected);

        List<ItemDto> actual = service.getItemByUserId(userId);

        assertThat(actual.getFirst()).isSameAs(expected);
    }

    @Test
    void searchByText() {
        String text = "text";
        Item returned = new Item();
        ItemDto expected = new ItemDto();
        when(itemRepository.findByText(text)).thenReturn(List.of(returned));
        when(mapper.toItemDto(returned)).thenReturn(expected);

        List<ItemDto> actual = service.searchByText(text, 1L);

        assertThat(actual.getFirst()).isSameAs(expected);
    }

    @Test
    void createComment_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createComment(new CommentDto(), 1L, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findById(1L);
        verifyNoInteractions(itemRepository, bookingRepository, commentRepository, mapper);
    }

    @Test
    void createComment_whenItemNotFound_shouldThrowException() {
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.createComment(new CommentDto(), 1L, 1L));

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verifyNoInteractions(bookingRepository, commentRepository, mapper);
    }

    @Test
    void createComment_whenNoPastBookings_shouldThrowValidationException() {
        User user = new User();
        Item item = new Item();
        CommentDto dto = new CommentDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> service.createComment(dto, 1L, 1L));

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).findByBookerIdAndItemId(1L, 1L);
        verifyNoInteractions(commentRepository, mapper);
    }

    @Test
    void createComment_whenValidData_shouldSaveAndReturnComment() {
        User user = new User();
        Item item = new Item();
        CommentDto inputDto = new CommentDto();
        Comment comment = new Comment();
        Comment savedComment = new Comment();
        CommentDto expectedDto = new CommentDto();
        Booking pastBooking = new Booking();
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(1L, 1L))
                .thenReturn(List.of(pastBooking));
        when(mapper.toComment(inputDto, item, user)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(mapper.toCommentDto(savedComment)).thenReturn(expectedDto);

        CommentDto result = service.createComment(inputDto, 1L, 1L);

        assertThat(expectedDto).isSameAs(result);

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).findByBookerIdAndItemId(1L, 1L);
        verify(mapper).toComment(inputDto, item, user);
        verify(commentRepository).save(comment);
        verify(mapper).toCommentDto(savedComment);
    }
}