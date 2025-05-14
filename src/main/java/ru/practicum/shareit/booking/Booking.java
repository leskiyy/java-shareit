package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Data
@Entity
@DynamicUpdate
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
}
