package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.DBIntegrationTestBase;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest extends DBIntegrationTestBase {

    private final ItemRequestService service;
    private final EntityManager entityManager;

    @Test
    void createRequest() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("test")
                .build();

        ItemRequestDto returned = service.createRequest(dto, 1L);

        TypedQuery<ItemRequest> query = entityManager.createQuery("select r from ItemRequest r where r.id = :id",
                ItemRequest.class);
        ItemRequest saved = query
                .setParameter("id", returned.getId())
                .getSingleResult();

        assertThat(saved)
                .hasFieldOrPropertyWithValue("description", "test");
        assertThat(saved.getCreated()).isBefore(LocalDateTime.now()).isAfter(LocalDateTime.now().minusMinutes(1));
    }

    @Test
    void getOwnersRequests() {
        List<ItemRequestDto> ownersRequestsUser1 = service.getOwnersRequests(1L);
        assertThat(ownersRequestsUser1).hasSize(2);
        for (ItemRequestDto dto : ownersRequestsUser1) {
            if (dto.getId().equals(1L)) {
                assertThat(dto.getItems()).hasSize(2);
            }
        }

        List<ItemRequestDto> ownersRequestsUser2 = service.getOwnersRequests(2L);
        assertThat(ownersRequestsUser2).hasSize(1).first()
                .hasFieldOrPropertyWithValue("description", "desc3")
                .hasFieldOrPropertyWithValue("id", 3L);
        assertThat(ownersRequestsUser2.getFirst().getItems()).hasSize(1).first()
                .hasFieldOrPropertyWithValue("id", 2L);

        List<ItemRequestDto> ownersRequests3 = service.getOwnersRequests(3L);
        assertThat(ownersRequests3).isEmpty();
    }

    @Test
    void getRequestById() {
        long requestId = 1L;
        long userId = 1L;
        ItemRequestDto requestById = service.getRequestById(requestId, userId);

        assertThat(requestById)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "desc1")
                .hasFieldOrPropertyWithValue("created", LocalDateTime.parse("2025-05-25T19:00"));
    }

    @Test
    void getAllRequests() {
        long userId = 1L;
        List<ItemRequestDto> allRequestsUser1 = service.getAllRequests(userId);

        assertThat(allRequestsUser1).hasSize(1);
        assertThat(allRequestsUser1).last().hasFieldOrPropertyWithValue("items", null);

        userId = 2L;
        List<ItemRequestDto> allRequestsUser2 = service.getAllRequests(userId);

        assertThat(allRequestsUser2).hasSize(2);
        assertThat(allRequestsUser2).last().hasFieldOrPropertyWithValue("items", null);
        assertThat(allRequestsUser2).first().hasFieldOrPropertyWithValue("items", null);

        userId = 3L;
        List<ItemRequestDto> allRequestsUser3 = service.getAllRequests(userId);

        assertThat(allRequestsUser3).hasSize(3);
        assertThat(allRequestsUser3).last().hasFieldOrPropertyWithValue("items", null);
        assertThat(allRequestsUser3).first().hasFieldOrPropertyWithValue("items", null);
    }
}