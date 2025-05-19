package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    ItemRequestService service;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestMapper mapper;

    @Test
    void createRequest_whenUserIsNotFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.createRequest(new ItemRequestDto(), userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createRequest_whenOK() {
        long userId = 1L;
        ItemRequestDto income = new ItemRequestDto();
        ItemRequest saved = new ItemRequest();
        ItemRequestDto expected = new ItemRequestDto();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(mapper.toItemRequest(income, userId)).thenReturn(saved);
        when(itemRequestRepository.save(saved)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(expected);


        ItemRequestDto actual = service.createRequest(income, userId);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void getOwnersRequests_whenUserIsNotFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.getOwnersRequests(userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOwnersRequests_whenOK() {
        long userId = 1L;
        ItemRequest returned = new ItemRequest();
        ItemRequestDto expected = new ItemRequestDto();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByAuthorId(userId)).thenReturn(List.of(returned));
        when(mapper.toDtoWithItems(returned)).thenReturn(expected);


        List<ItemRequestDto> actual = service.getOwnersRequests(userId);

        assertThat(actual).hasSize(1).first().isSameAs(expected);
    }

    @Test
    void getRequestById_whenUserIsNotFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.getRequestById(1L, userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequestById_whenOK() {
        long userId = 1L;
        long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(userId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> service.getRequestById(requestId, userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequestById_whenRequestInNotFound() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequest returned = new ItemRequest();
        ItemRequestDto expected = new ItemRequestDto();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(userId)).thenReturn(Optional.of(returned));
        when(mapper.toDtoWithItems(returned)).thenReturn(expected);


        ItemRequestDto actual = service.getRequestById(requestId, userId);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    void getAllRequests_whenUserIsOntFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.getAllRequests(userId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllRequests_whenOK() {
        long userId = 1L;
        ItemRequest returned = new ItemRequest();
        ItemRequestDto expected = new ItemRequestDto();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByAuthorIdNotOrderByCreated(userId)).thenReturn(List.of(returned));
        when(mapper.toDto(returned)).thenReturn(expected);


        List<ItemRequestDto> actual = service.getAllRequests(userId);

        assertThat(actual).hasSize(1).first().isSameAs(expected);
    }
}