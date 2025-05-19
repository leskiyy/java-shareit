package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemIdOrderByStart(long itemId);

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerIdAndItemId(long bookerId, long itemId);

    List<Booking> findByItemOwnerId(long ownerId);
}
