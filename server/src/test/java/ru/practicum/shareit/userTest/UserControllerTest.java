package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        userDto = createValidUserDtoExample();
    }

    //Пример валидного пользователя
    private UserDto createValidUserDtoExample() {
        return new UserDto(1L, "test", "test@yandex.ru");
    }

    //Создание пользователя
    @Test
    public void createValidUser() throws Exception {
        when(userService.create(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users").content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test\",\"email\": \"test@yandex.ru\"}"));

        verify(userService, times(1)).create(userDto);
    }

    //Получение пользователя по идентификатору
    @Test
    public void getUserById() throws Exception {
        Long userDtoId = userDto.getId();

        when(userService.getUser(userDtoId)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test\",\"email\": \"test@yandex.ru\"}"));

        verify(userService, times(1)).getUser(userDtoId);
    }

    //Получение всех пользователей
    @Test
    public void getAllUsers() throws Exception {
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);

        when(userService.getAll()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"test\",\"email\": \"test@yandex.ru\"}]"));

        verify(userService, times(1)).getAll();
    }

    //Обновление данных пользователя
    @Test
    public void updateValidUser() throws Exception {
        UserDto userDto1 = createValidUserDtoExample();

        Long userDtoId = userDto.getId();
        userDto1.setName("test1");

        userService.create(userDto);

        when(userService.update(userDtoId, userDto1)).thenReturn(userDto1);

        mockMvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test1\",\"email\": \"test@yandex.ru\"}"));

        verify(userService, times(1)).update(userDtoId, userDto1);
    }

    // Удаление пользователя
    @Test
    public void deleteUser() throws Exception {
        Long userDtoId = userDto.getId();

        userService.create(userDto);

        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());

        verify(userService, times(1)).delete(userDtoId);
    }
}