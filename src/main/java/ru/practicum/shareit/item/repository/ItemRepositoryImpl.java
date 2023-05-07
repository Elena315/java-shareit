package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemList = new HashMap<>();
    private Long itemId = 0L;

    //Создание вещи
    @Override
    public Item create(Item item) {
        item.setId(++itemId);
        itemList.put(item.getId(), item);
        return item;
    }

    //Получение вещи
    @Override
    public Optional<Item> getItem(Long itemId) {
        return Optional.ofNullable(itemList.get(itemId));
    }

    //Обновление вещи
    @Override
    public Item update(Item item) {
        itemList.put(item.getId(), item);
        return item;
    }

    //Получение всех вещей
    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemList.values());
    }

    @Override
    public List<Item> search(String text) {
        final String searchText = text.toLowerCase();

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        Predicate<Item> inName = item -> item.getName().toLowerCase().contains(searchText);
        Predicate<Item> inDesc = item -> item.getDescription().toLowerCase().contains(searchText);

        return getAll()
                .stream()
                .filter(inName.or(inDesc))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}