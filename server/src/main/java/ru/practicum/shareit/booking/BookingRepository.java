package ru.practicum.shareit.booking;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Fetch(value = FetchMode.JOIN)
    List<Booking> findByItemIdOrderByStart(long itemId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerIdAndItemId(long bookerId, long itemId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItemOwnerId(long ownerId);
}
