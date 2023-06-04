package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = createValidUserExample();
    }

    //Пример валидного пользователя
    private User createValidUserExample() {
        return new User(1L, "test", "test@yandex.ru");
    }

    //Создание пользователя
    @Test
    public void createValidUser() {
        Long userId = user.getId();

        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = userService.create(UserMapper.toUserDto(user));

        assertEquals(userId, userDto.getId(), "Идентификаторы не совпадают");
        assertEquals(user.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDto.getEmail(), "Почты не совпадают");

        verify(userRepository, times(1)).save(user);
    }

    //Получение пользователя по идентификатору
    @Test
    public void getUserById() {
        Long userId = user.getId();

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.create(UserMapper.toUserDto(user));
        UserDto userDto = userService.getUser(userId);

        assertEquals(userId, userDto.getId(), "Идентификаторы не совпадают");
        assertEquals(user.getName(), userDto.getName(), "Имя пользователя не совпадает");
        assertEquals(user.getEmail(), userDto.getEmail(), "Почта пользователя не совпадает");

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findById(userId);
    }

    //Получение всех пользователей
    @Test
    public void getAllUsers() {
        Long userId = user.getId();

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        userService.create(UserMapper.toUserDto(user));
        final List<UserDto> userDtoList = userService.getAll();

        assertEquals(userDtoList.size(), 1, "Пользователь отсутствует");
        assertEquals(userId, userDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(user.getName(), userDtoList.get(0).getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDtoList.get(0).getEmail(), "Почты не спадают");

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findAll();
    }

    //Обновление данных пользователя
    @Test
    public void updateValidUser() {
        User user1 = createValidUserExample();

        Long userId = user.getId();
        user1.setName("test1");

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.update(userId, UserMapper.toUserDto(user1));

        assertEquals(userId, userDto.getId(), "Идентификаторы не совпадают");
        assertEquals(user1.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDto.getEmail(), "Почты не совпадают");

        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(1)).findById(userId);
    }

    // Удаление пользователя
    @Test
    public void deleteUser() {
        Long userId = user.getId();

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.create(UserMapper.toUserDto(user));
        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    //Получение несуществующего пользователя
    @Test
    public void getUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }

    //Обновление несуществующего пользователя
    @Test
    public void updateUnknownUser() {
        User user1 = createValidUserExample();
        user1.setName("test1");

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                userService.update(user1.getId(), UserMapper.toUserDto(user1)));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Неверный идентификатор пользователя");
    }
}