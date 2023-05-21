package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null);
    }

    public static ItemDtoBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoBooking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
    }
}