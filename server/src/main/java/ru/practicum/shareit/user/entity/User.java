package ru.practicum.shareit.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@ToString(exclude = "requests")
@EqualsAndHashCode(exclude = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<ItemRequest> requests;
}
