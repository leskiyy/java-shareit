package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findAllByAuthorId(long authorId);

    List<ItemRequest> findAllByAuthorIdNotOrderByCreated(long authorId);
}
