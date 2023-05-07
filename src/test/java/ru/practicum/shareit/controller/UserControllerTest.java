package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItTests;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest extends ShareItTests {
    private final UserController userController;

    //Создание валидного пользователя
    @Test
    public void createValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");

        userController.create(userDto);

        assertEquals(userController.getUser(1L), userDto, "Пользователь не создался");
    }

    //Создание пользователя с пустым именем
    @Test
    public void createUserWithEmptyName() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@yandex.ru");

        String validatorMessage = validator.validate(userDto).iterator().next().getMessage();

        assertEquals("Имя пользователя не может быть пустым", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Создание пользователя с пустым email
    @Test
    public void createUserWithEmptyEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовое наименование");

        String validatorMessage = validator.validate(userDto).iterator().next().getMessage();

        assertEquals("Некорректно заполнен email пользователя", validatorMessage,
                "Текст ошибки валидации разный");
    }

    //Создание пользователя с повторяющимся email
    @Test
    public void createUserWithRepeatEmail() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        UserDto userDto1 = new UserDto();
        userDto1.setId(2L);
        userDto1.setName("Тестовое наименование1");
        userDto1.setEmail("test@yandex.ru");

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.create(userDto1));

        assertEquals("Такой email уже используется", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление несуществующего пользователя
    @Test
    public void updateUnknownUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");

        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("Тестовое наименование1");
        userDto1.setEmail("test1@yandex.ru");

        Throwable throwable = assertThrows(NotFoundException.class, () -> userController.update(2L, userDto1));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление пользователя
    @Test
    public void updateValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("Тестовое наименование");
        userDto1.setEmail("test1@yandex.ru");
        userController.update(1L, userDto1);

        assertEquals(userController.getUser(1L), userDto1, "Пользователь не обновился");
    }

    //Создание пользователя с повторяющимся email
    @Test
    public void updateUserWithRepeatEmail() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        UserDto userDto1 = new UserDto();
        userDto1.setId(2L);
        userDto1.setName("Тестовое наименование1");
        userDto1.setEmail("test@yandex.ru");

        Throwable throwable = assertThrows(ValidationException.class, () -> userController.create(userDto1));

        assertEquals("Такой email уже используется", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Удаление существующего пользователя
    @Test
    public void deleteValidUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");

        userController.create(userDto);
        userController.delete(1L);

        List<UserDto> userDtoList = userController.getAll();

        assertEquals(userDtoList.size(), 0, "Пользователь не удален");
    }

    //Удаление существующего пользователя
    @Test
    public void deleteUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> userController.delete(1L));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение пользователя
    @Test
    public void getValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");

        userController.create(userDto);
        UserDto userDto1 = userController.getUser(1L);

        assertEquals(userDto1, userDto, "Пользователи не совпадают");
    }

    //Получение неизвестного пользователя
    @Test
    public void getUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> userController.getUser(1L));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение всех пользователей
    @Test
    public void getAllUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");

        UserDto userDto1 = new UserDto();
        userDto1.setName("Тестовое наименование1");
        userDto1.setEmail("test1@yandex.ru");

        userController.create(userDto);
        userController.create(userDto1);

        List<UserDto> userDtoList = userController.getAll();

        assertEquals(userDtoList.size(), 2, "Количество пользователей не совпадает");
    }
}