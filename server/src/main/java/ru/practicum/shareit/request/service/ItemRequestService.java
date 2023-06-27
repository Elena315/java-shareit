package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoWithItems> getAll(Long userId);

    ItemRequestDtoWithItems getItemRequest(Long userId, Long itemRequestId);

    List<ItemRequestDtoWithItems> getAllWithPageable(Long userId, Integer from, Integer size);
}