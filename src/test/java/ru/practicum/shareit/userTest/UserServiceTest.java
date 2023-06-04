package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

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