package ru.practicum.shareit.itemTest;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Item item;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
        item = createValidItemExample();
    }

    private Item createValidItemExample() {
        User user = new User(1L, "testUser", "test@yandex.ru");
        User user1 = new User(2L, "testUser1", "test1@yandex.ru");

        ItemRequest itemRequest = new ItemRequest(1L, "testItemRequest", user1, LocalDateTime.now());

        item = new Item(1L, "testItem", "itemDescription", true, user, itemRequest);
        return item;
    }

    private CommentDto createValidCommentDtoExample() {
        return new CommentDto(1L, "testComment", "testUser", LocalDateTime.now());
    }

    //Создание вещи
    @Test
    public void createValidItem() throws Exception {
        Long userId = item.getOwner().getId();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items").content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"itemDescription\", \"available\": true, \"requestId\": 1}"));

        verify(itemService, times(1)).create(userId, itemDto);
    }

    //Обновление вещи
    @Test
    public void updateItem() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        Item item1 = createValidItemExample();
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);

        itemDto1.setName("testItem1");

        itemService.create(item.getOwner().getId(), itemDto);

        when(itemService.update(item.getOwner().getId(), itemDto.getId(), itemDto1)).thenReturn(itemDto1);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"testItem1\"," +
                        " \"description\": \"itemDescription\", \"available\": true, \"requestId\": 1}"));

        verify(itemService, times(1)).update(item.getOwner().getId(), itemDto.getId(), itemDto1);
    }

    //Получение вещи
    @Test
    public void getItemById() throws Exception {
        Long userId = item.getOwner().getId();
        Long itemId = item.getId();

        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoWithBooking(item);

        when(itemService.getItem(itemId, userId)).thenReturn(itemDtoBooking);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", item.getOwner().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"itemDescription\", \"available\": true," +
                        " \"lastBooking\": null, \"nextBooking\": null, \"comments\": []}"));
        verify(itemService, times(1)).getItem(itemId, userId);
    }

    //Получение всех вещей пользователя
    @Test
    public void getAllItemsByUserId() throws Exception {
        List<ItemDtoBooking> items = new ArrayList<>();

        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoWithBooking(item);
        items.add(itemDtoBooking);

        when(itemService.getAllItemsByUser(item.getOwner().getId(), 0, 20)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"itemDescription\", \"available\": true," +
                        " \"lastBooking\": null, \"nextBooking\": null, \"comments\": []}]"));
        verify(itemService, times(1))
                .getAllItemsByUser(item.getOwner().getId(), 0, 20);
    }

    //Поиск вещи
    @Test
    public void search() throws Exception {
        List<ItemDto> items = new ArrayList<>();

        ItemDto itemDto = ItemMapper.toItemDto(item);
        items.add(itemDto);

        String text = item.getDescription().substring(0, 3);

        when(itemService.search(text, 0, 20)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"itemDescription\", \"available\": true, \"requestId\": 1}]"));
        verify(itemService, times(1)).search(text, 0, 20);
    }

    //Создание комментария к вещи
    @Test
    public void createCommentForItem() throws Exception {
        CommentDto commentDto = createValidCommentDtoExample();

        when(itemService.createComment(item.getOwner().getId(), item.getId(), commentDto))
                .thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"text\": \"testComment\"," +
                        " \"authorName\": \"testUser\"}"));
        verify(itemService, times(1))
                .createComment(item.getOwner().getId(), item.getId(), commentDto);
    }
}
