package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST/items - Создана вещь {} у пользователя с id = {}", itemDto.getName(), userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH/items/id - Обновлена вещь с id = {}", itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {
        log.info("GET/items/id - Получена вещь с id = {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItemsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех вещей пользователя с id = {}", userId);
        return itemService.getAllItemsByUser(userId);
    }

    @GetMapping("search")
    public List<ItemDto> search(
            @RequestParam(required = false) String text) {
        return itemService.search(text);
    }
}