package ru.practicum.shareit.item.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = {"bookings", "comments"})
@ToString(exclude = {"bookings", "comments"})
@DynamicUpdate
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "item")
    @OrderBy("start")
    private List<Booking> bookings;
    @OneToMany(mappedBy = "item")
    @OrderBy("created")
    private List<Comment> comments;
}
