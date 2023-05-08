package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    //Создание пользователя
    @Override
    public User create(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        log.info("Создан пользователь с id = {}", user.getId());
        return user;
    }

    //Обновление пользователя
    @Override
    public User update(User user) {
        if (users.containsKey(userId)) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь с id = {}", user.getId());
            return user;
        } else {
            throw new NotFoundException("Неверный идентификатор пользователя");
        }
    }

    //Удаление пользователя
    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("Пользователь с id = {} удален", userId);
    }

    //Получение пользователя
    @Override
    public Optional<User> getUser(Long userId) {
        log.info("Поиск пользователя с id = {}", userId);
        return Optional.ofNullable(users.get(userId));
    }

    //Получение всех пользователей
    @Override
    public List<User> getAll() {
        log.info("Запрос списка пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public Map<Long, User> getAllUsers() {
        log.info("Запрос списка пользователей c ID");
        return users;
    }
}