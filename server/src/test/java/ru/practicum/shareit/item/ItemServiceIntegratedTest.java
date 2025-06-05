package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.DBIntegrationTestBase;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegratedTest extends DBIntegrationTestBase {

    private final ItemService service;
    private final EntityManager entityManager;

    @Test
    void createItem() {
        long userId = 1L;
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("test name")
                .available(true)
                .description("test desc")
                .build();

        ItemDto item = service.createItem(dto, userId);

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item actual = query
                .setParameter("id", item.getId())
                .getSingleResult();

        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", "test name")
                .hasFieldOrPropertyWithValue("description", "test desc")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void update_whenUserIsOwner() {
        long itemId = 1L;
        long userId = 1L;
        ItemCreateDto dto = ItemCreateDto.builder()
                .available(false)
                .description("test desc")
                .build();

        service.update(dto, userId, itemId);

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item actual = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "test desc")
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    void update_whenUserIsNotOwner() {
        long itemId = 1L;
        long userId = 2L;
        ItemCreateDto dto = ItemCreateDto.builder()
                .available(false)
                .description("test desc")
                .build();

        assertThatThrownBy(() -> service.update(dto, userId, itemId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Can't change item's owner");

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item actual = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "desc1")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void getItemById_whenUserIsOwner() {
        ItemDto item = service.getItemById(1L, 1L);

        System.out.println(item);

        assertThat(item)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "desc1")
                .hasFieldOrPropertyWithValue("available", true);
        assertThat(item.getLastBooking())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", LocalDateTime.parse("2021-05-26T19:00"))
                .hasFieldOrPropertyWithValue("end", LocalDateTime.parse("2021-05-27T19:00"));
        assertThat(item.getNextBooking())
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("start", LocalDateTime.parse("2035-05-26T19:00"))
                .hasFieldOrPropertyWithValue("end", LocalDateTime.parse("2035-05-27T19:00"));
    }

    @Test
    void getItemByUserId() {
        ItemDto item = service.getItemById(1L, 2L);

        System.out.println(item);

        assertThat(item)
                .hasFieldOrPropertyWithValue("name", "item1")
                .hasFieldOrPropertyWithValue("description", "desc1")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("lastBooking", null);
    }

    @Test
        //8 запросов, надо поиграться с графами
    void searchByText() {
        String text = "target";
        List<ItemDto> items = service.searchByText(text, 1L);

        assertThat(items).hasSize(2);
        assertThat(items.getFirst().getName().contains(text) ||
                   items.getFirst().getDescription().contains(text)).isTrue();
        assertThat(items.getLast().getName().contains(text) ||
                   items.getLast().getDescription().contains(text)).isTrue();
    }

    @Test
    void createComment_whenCommentatorHasNotBookedItem() {
        CommentDto dto = CommentDto.builder()
                .text("text")
                .build();
        long userId = 3L;
        long itemId = 2L;

        assertThatThrownBy(() -> service.createComment(dto, userId, itemId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("User has not ever booked item");
    }

    @Test
    void createComment_whenCommentatorHasBookedItem() {
        CommentDto dto = CommentDto.builder()
                .text("text")
                .build();
        long userId = 3L;
        long itemId = 1L;

        CommentDto comment = service.createComment(dto, userId, itemId);

        TypedQuery<Comment> query = entityManager.createQuery("select c from Comment c where c.id = :id",
                Comment.class);
        Comment saved = query.setParameter("id", comment.getId())
                .getSingleResult();

        assertThat(saved.getText()).isEqualTo("text");
        assertThat(saved.getItem().getId()).isEqualTo(itemId);
        assertThat(saved.getAuthor().getId()).isEqualTo(userId);
    }
}