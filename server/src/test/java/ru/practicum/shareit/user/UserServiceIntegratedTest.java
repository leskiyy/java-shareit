package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.DBIntegrationTestBase;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegratedTest extends DBIntegrationTestBase {

    private final UserService service;
    private final EntityManager entityManager;

    @Test
    void getAllUsers() {
        List<UserDto> users = service.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void getUserById_whenFound() {
        UserDto user = service.getUserById(1L);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name1")
                .hasFieldOrPropertyWithValue("email", "email1");
    }

    @Test
    void getUserById_whenNotFound() {
        assertThatThrownBy(() -> service.getUserById(5L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveUser_whenOK() {

        UserDto saved = service.saveUser(UserDto.builder()
                .name("name4")
                .email("email4@1.ru")
                .build());


        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User actual = query
                .setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(actual)
                .hasFieldOrPropertyWithValue("name", "name4")
                .hasFieldOrPropertyWithValue("email", "email4@1.ru");

    }

    @Test
    void saveUser_withDuplicateEmail() {
        assertThatThrownBy(() ->
                service.saveUser(UserDto.builder()
                        .name("name4")
                        .email("email1")
                        .build()))
                .isInstanceOf(EmailConflictException.class);
    }

    @Test
    void updateUser_whenOK() {
        service.updateUser(UserDto.builder()
                .name("updated")
                .email("newEmail")
                .build(), 1L);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User actual = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(actual).hasFieldOrPropertyWithValue("name", "updated")
                .hasFieldOrPropertyWithValue("email", "newEmail");
    }

    @Test
    void updateUser_whenNotFound() {
        assertThatThrownBy(() -> service.updateUser(UserDto.builder()
                .name("updated")
                .email("newEmail")
                .build(), 5L)).isInstanceOf(NotFoundException.class)
                .hasMessage("There is no user with id=5");
    }

    @Test
    void updateUser_whenDuplicateEmail() {

        assertThatThrownBy(() -> {
            service.updateUser(UserDto.builder()
                    .name("updated")
                    .email("email2")
                    .build(), 1L);
            entityManager.flush();
        })
                .isInstanceOf(EmailConflictException.class);

    }

    @Test
    void deleteUserById() {
        service.deleteUserById(1L);

        TypedQuery<Long> query = entityManager.createQuery("Select count(u) from User u where u.id = :id", Long.class);
        Long count = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(count).isEqualTo(0L);
    }
}