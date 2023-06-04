package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItTests;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest extends ShareItTests {
    private final ItemController itemController;
    private final UserController userController;

    //Создание вещи
    @Test
    public void createValidItemWithValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        ItemDtoBooking itemDto1 = itemController.getItem(1L, 1L);

        assertEquals(itemDto1.getId(), itemDto.getId(), "Вещь не создалась");
    }

    //Создание с неизвестным пользователем
    @Test
    public void createValidItemWithUnknownUser() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemController.create(1L, itemDto));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Создание вещи c пустым именем
    @Test
    public void createValidItemWithEmptyName() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);

        Throwable throwable =
                assertThrows(DataIntegrityViolationException.class, () -> itemController.create(1L, itemDto));

        assertNotNull(throwable.getMessage());
    }

    //Создание вещи c пустым описанием
    @Test
    public void createValidItemWithEmptyDescription() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setAvailable(true);

        Throwable throwable =
                assertThrows(DataIntegrityViolationException.class, () -> itemController.create(1L, itemDto));

        assertNotNull(throwable.getMessage());
    }

    //Создание вещи c пустым статусом
    @Test
    public void createValidItemWithNullAvailable() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(null);

        Throwable throwable =
                assertThrows(DataIntegrityViolationException.class, () -> itemController.create(1L, itemDto));

        assertNotNull(throwable.getMessage());
    }

    //Обновление вещи
    @Test
    public void updateValidItemWithValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Тестовое наименование1");
        itemDto1.setDescription("Тестовое описание1");
        itemDto1.setAvailable(true);
        itemController.update(1L, 1L, itemDto1);

        ItemDtoBooking itemDto2 = itemController.getItem(1L, 1L);

        assertEquals(itemDto2.getId(), itemDto1.getId(), "Вещь не обновилась");
    }

    //Обновление чужой вещи
    @Test
    public void updateAlienItemWithValidUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Тестовое наименование1");
        itemDto1.setDescription("Тестовое описание1");
        itemDto1.setAvailable(true);

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemController.update(2L, 1L, itemDto1));

        assertEquals("Нельзя изменить чужую вещь", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение существующей вещи
    @Test
    public void getValidItem() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        ItemDtoBooking itemDto1 = itemController.getItem(1L, 1L);

        assertEquals(itemDto1.getId(), itemDto.getId(), "Вещи не совпадают");
    }

    //Получение несуществующей вещи
    @Test
    public void getUnknownItem() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> itemController.getItem(1L, 1L));

        assertEquals("Неверный идентификатор вещи", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение всех вещей пользователя
    @Test
    public void getAllItems() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(2L);
        itemDto1.setName("Тестовое наименование1");
        itemDto1.setDescription("Тестовое описание1");
        itemDto1.setAvailable(true);
        itemController.create(1L, itemDto1);

        List<ItemDtoBooking> itemDtoList = itemController.getAllItemsByUser(1L);

        assertEquals(itemDtoList.size(), 2, "Количество вещей не совпадает");
    }

    //Поиск вещи по названию
    @Test
    public void searchItemName() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        List<ItemDto> itemDtoList = itemController.search("Тест");

        assertEquals(itemDtoList.size(), 1, "Вещь по названию не найдена");
    }

    //Поиск вещи по названию
    @Test
    public void searchItemDescription() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Тестовое наименование");
        userDto.setEmail("test@yandex.ru");
        userController.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Тестовое наименование");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);
        itemController.create(1L, itemDto);

        List<ItemDto> itemDtoList = itemController.search("ОПисаН");

        assertEquals(itemDtoList.size(), 1, "Вещь по описанию не найдена");
    }

    //Поиск несуществующей вещи
    @Test
    public void searchUnknownItem() {
        List<ItemDto> itemDtoList = itemController.search("Те");

        assertEquals(itemDtoList.size(), 0, "Вещь найдена");
    }
}