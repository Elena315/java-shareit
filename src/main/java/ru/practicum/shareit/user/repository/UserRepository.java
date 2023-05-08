package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user);

    void delete(Long userId);

    Optional<User> getUser(Long userId);

    List<User> getAll();

    Map<Long, User> getAllUsers();
}