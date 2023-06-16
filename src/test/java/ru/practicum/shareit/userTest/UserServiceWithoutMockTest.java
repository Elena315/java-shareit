package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Profile("test")
public class UserServiceWithoutMockTest {
    private final UserService userService;
    private final User user = new User(1L, "test", "test@yandex.ru");

    //Создание пользователя без моков
    @Test
    public void createValidUserNoMock() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));

        assertEquals(user.getId(), userDto.getId(), "Идентификаторы не совпадают");
        assertEquals(user.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDto.getEmail(), "Почты не совпадают");
    }

    //Получение пользователя
    @Test
    public void getUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        UserDto userDto1 = userService.getUser(userDto.getId());

        assertEquals(user.getId(), userDto1.getId(), "Идентификаторы не совпадают");
        assertEquals(user.getName(), userDto1.getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDto1.getEmail(), "Почты не совпадают");
    }
}