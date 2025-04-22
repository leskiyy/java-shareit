package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Set<String> emails = ConcurrentHashMap.newKeySet();
    private static final AtomicLong id = new AtomicLong(0);

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailConflictException(String.format("User with email %s already exists", user.getEmail()));
        }
        Long id = nextId();
        user.setId(id);
        users.put(id, user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User old = users.get(user.getId());
        if (old == null) {
            throw new NotFoundException("There is no user with id=" + user.getId());
        }

        if (user.getEmail() != null) {
            if (!old.getEmail().equals(user.getEmail()) &&
                emails.contains(user.getEmail())) {
                throw new EmailConflictException(String.format("User with email %s already exists", user.getEmail()));
            }
            old.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            old.setName(user.getName());
        }


        return old;
    }

    @Override
    public void deleteById(long id) {
        User removed = users.remove(id);
        if (removed != null) {
            emails.remove(removed.getEmail());
        }
    }

    @Override
    public boolean existById(long id) {
        return users.get(id) != null;
    }

    private long nextId() {
        return id.incrementAndGet();
    }
}
