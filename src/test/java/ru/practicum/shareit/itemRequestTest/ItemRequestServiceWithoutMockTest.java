package ru.practicum.shareit.itemRequestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Profile("test")
public class ItemRequestServiceWithoutMockTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final User user = new User(1L, "test", "test@yandex.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "itemRequestDescription", user,
            LocalDateTime.now());

    //Создание запроса
    @Test
    public void createValidItemRequest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));

        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest),
                userDto.getId());

        assertEquals(itemRequest.getId(), itemRequestDto.getId(), "Идентификаторы не совпадают");
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription(), "Описания не совпадают");
    }

    //Получение запроса
    @Test
    public void getItemRequest() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));

        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequestMapper.toItemRequestDto(itemRequest),
                userDto.getId());

        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestService.getItemRequest(userDto.getId(),
                itemRequestDto.getId());

        assertEquals(itemRequest.getId(), itemRequestDtoWithItems.getId(), "Идентификаторы не совпадают");
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.getDescription(),
                "Описания не совпадают");
    }
}