package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Создана вещь с id = {} у пользователя с id = {}", itemDto.getId(), userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Обновлена вещь с id = {}", id);
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получена вещь с id = {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Получение всех вещей пользователя с id = {}", text);
        return itemService.search(text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}