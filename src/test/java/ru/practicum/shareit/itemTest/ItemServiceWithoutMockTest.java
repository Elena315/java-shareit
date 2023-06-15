package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Profile("test")
public class ItemServiceWithoutMockTest {
    private final ItemService itemService;
    private final UserService userService;
    private final User user = new User(1L, "testUser", "test@yandex.ru");
    private final Item item = new Item(1L, "testItem", "itemDescription", true, user,
            null);

    //Создание вещи
    @Test
    public void createValidItem() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        ItemDto itemDto = itemService.create(userDto.getId(), ItemMapper.toItemDto(item));

        assertEquals(item.getId(), itemDto.getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDto.getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDto.getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDto.getAvailable(), "Статусы не совпадают");
    }

    //Получение вещи
    @Test
    public void getItem() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        ItemDto itemDto = itemService.create(userDto.getId(), ItemMapper.toItemDto(item));

        ItemDtoBooking itemDto1 = itemService.getItem(itemDto.getId(), userDto.getId());

        assertEquals(item.getId(), itemDto1.getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDto1.getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDto1.getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDto1.getAvailable(), "Статусы не совпадают");
    }
}