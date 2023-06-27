package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoBooking getItem(Long itemId, Long userId);

    List<ItemDtoBooking> getAllItemsByUser(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}