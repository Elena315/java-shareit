package ru.practicum.shareit.itemRequestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ItemRequestMapper itemRequestMapper;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRequestMapper = new ItemRequestMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
        itemRequest = createItemRequestExample();
    }

    private ItemRequest createItemRequestExample() {
        User user = new User(2L, "test", "test@yandex.ru");
        return new ItemRequest(1L, "itemRequestDescription", user, LocalDateTime.now());
    }

    //Создание запроса
    @Test
    public void createValidItemRequest() throws Exception {
        Long userId = itemRequest.getRequestor().getId();

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        when(itemRequestService.create(itemRequestDto, userId)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        " \"description\": \"itemRequestDescription\"}"));

        verify(itemRequestService, times(1)).create(itemRequestDto, userId);
    }

    //Получение запроса
    @Test
    void getItemRequest() throws Exception {
        ItemRequest itemRequest = createItemRequestExample();
        Long itemRequestId = itemRequest.getId();
        Long userId = itemRequest.getRequestor().getId();

        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestMapper.toItemRequestDtoWithItems(itemRequest);

        when(itemRequestService.getItemRequest(userId, itemRequestId)).thenReturn(itemRequestDtoWithItems);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", itemRequest.getRequestor().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        " \"description\": \"itemRequestDescription\"}"));

        verify(itemRequestService, times(1)).getItemRequest(userId, itemRequestId);
    }

    //Получение всех вещей
    @Test
    void getAllItemRequests() throws Exception {
        ItemRequest itemRequest = createItemRequestExample();
        Long userId = itemRequest.getRequestor().getId();

        List<ItemRequestDtoWithItems> itemRequestDtoWithItemsList = new ArrayList<>();

        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestMapper.toItemRequestDtoWithItems(itemRequest);
        itemRequestDtoWithItemsList.add(itemRequestDtoWithItems);

        when(itemRequestService.getAll(userId)).thenReturn(itemRequestDtoWithItemsList);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", itemRequest.getRequestor().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        " \"description\": \"itemRequestDescription\"}]"));

        verify(itemRequestService, times(1)).getAll(userId);
    }

    //Получение всех запросов с страниц
    @Test
    void getAllItemRequestWithPageable() throws Exception {
        ItemRequest itemRequest = createItemRequestExample();
        Long userId = itemRequest.getRequestor().getId();

        List<ItemRequestDtoWithItems> itemRequestDtoWithItemsList = new ArrayList<>();

        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestMapper.toItemRequestDtoWithItems(itemRequest);
        itemRequestDtoWithItemsList.add(itemRequestDtoWithItems);

        when(itemRequestService.getAllWithPageable(userId, 0, 20)).thenReturn(itemRequestDtoWithItemsList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", itemRequest.getRequestor().getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        " \"description\": \"itemRequestDescription\"}]"));

        verify(itemRequestService, times(1)).getAllWithPageable(userId, 0, 20);
    }
}