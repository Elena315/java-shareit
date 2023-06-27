package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Создан пользователь с id = {}", userDto.getId());
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Min(1) @NotNull @PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновлен пользователь с id = {}", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Min(1) @NotNull @PathVariable Long userId) {
        log.info("Удален пользователь с id = {}", userId);
        return userClient.delete(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Min(1) @NotNull @PathVariable Long userId) {
        log.info("Получен пользователь с id = {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }
}