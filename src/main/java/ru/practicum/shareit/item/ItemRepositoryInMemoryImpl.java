package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private static Long id = 0L;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findByUserId(long userId) {
        return new ArrayList<>(items.values()).stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        long id = nextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item old = items.get(item.getId());
        if (!Objects.equals(old.getOwner(), item.getOwner())) {
            throw new ValidationException("Can't change item's userId");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            old.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            old.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            old.setAvailable(item.getAvailable());
        }
        return old;
    }

    @Override
    public boolean existById(long id) {
        return items.get(id) != null;
    }

    @Override
    public List<Item> findByText(String text) {
        return findAll().stream()
                .filter(item -> item.getAvailable() &&
                                (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                 item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }

    private long nextId() {
        return ++id;
    }
}
