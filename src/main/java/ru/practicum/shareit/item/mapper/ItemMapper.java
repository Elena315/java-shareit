package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                null);
        ItemRequest itemRequest = item.getItemRequest();

        if (itemRequest != null) {
            itemDto.setRequestId(itemRequest.getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null, null);
    }

    public static ItemDtoBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoBooking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                null, null, new ArrayList<>()
        );
    }
}