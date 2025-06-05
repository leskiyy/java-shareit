package ru.practicum.shareit.item.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = {"bookings", "comments", "itemRequest"})
@ToString(exclude = {"bookings", "comments", "itemRequest"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_request_id")
    private ItemRequest itemRequest;
}
