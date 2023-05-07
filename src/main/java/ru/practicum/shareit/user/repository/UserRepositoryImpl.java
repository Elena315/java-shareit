package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userList = new HashMap<>();
    private Long userId = 0L;

    //Создание пользователя
    @Override
    public User create(User user) {
        user.setId(++userId);
        userList.put(user.getId(), user);
        return user;
    }

    //Обновление пользователя
    @Override
    public User update(User user) {
        userList.put(user.getId(), user);
        return user;
    }

    //Удаление пользователя
    @Override
    public void delete(Long userId) {
        userList.remove(userId);
    }

    //Получение пользователя
    @Override
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(userList.get(userId));
    }

    //Получение всех пользователей
    @Override
    public List<User> getAll() {
        return new ArrayList<>(userList.values());
    }
}