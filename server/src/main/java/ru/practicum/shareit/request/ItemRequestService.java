package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    public ItemRequestDto createRequest(ItemRequestDto dto, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }

        ItemRequest saved = requestRepository.save(mapper.toItemRequest(dto, userId));

        return mapper.toDto(saved);
    }

    public List<ItemRequestDto> getOwnersRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        List<ItemRequest> usersRequests = requestRepository.findAllByAuthorId(userId);
        return usersRequests.stream()
                .map(mapper::toDtoWithItems)
                .toList();
    }

    public ItemRequestDto getRequestById(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("There is no request with id=" + requestId));
        return mapper.toDtoWithItems(request);
    }

    public List<ItemRequestDto> getAllRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("There is no user with id=" + userId);
        }
        return requestRepository.findAllByAuthorIdNotOrderByCreated(userId).stream()
                .map(mapper::toDto)
                .toList();
    }
}
