package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Optional<Item> getItem(Long itemId);

    Item update(Item item);

    List<Item> getAll();

    List<Item> search(String text);
}