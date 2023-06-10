package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
 class ItemServiceTest {
    private ItemService itemService;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private Item item;
    private User user;

    @BeforeEach
    public void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
        item = createValidItemExample();
    }

    private Item createValidItemExample() {
        user = new User(1L, "testUser", "test@yandex.ru");
        User user1 = new User(2L, "testUser1", "test1@yandex.ru");
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

        ItemRequest itemRequest = new ItemRequest(1L, "testItemRequest", user1, now);
        return new Item(1L, "testItem", "itemDescription", true, user, itemRequest);
    }

    private Comment createValidCommentExample(Item item, User user) {
        return new Comment(1L, "testComment", item, user, LocalDateTime.now());
    }

    private Booking createValidBookingExample(Item item, User user) {
        return new Booking(1L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2), item, user,
                Status.APPROVED);
    }

    //Создание вещи
    @Test
     void createValidItem() {
       Long itemId = item.getId();
       Long userId = item.getOwner().getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));

        when(itemRequestRepository.findById(item.getItemRequest().getId())).thenReturn(Optional.of(item.getItemRequest()));

        when(itemRepository.save(item)).thenReturn(item);

        ItemDto itemDto = itemService.create(userId, ItemMapper.toItemDto(item));

        assertEquals(itemId, itemDto.getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDto.getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDto.getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDto.getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).save(item);
    }

    //Обновление вещи
    @Test
     void updateItem() {
        Item item1 = createValidItemExample();
        Long itemId = item.getId();

        item1.setName("testItem2");

        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.update(user.getId(), itemId, ItemMapper.toItemDto(item1));

        assertEquals(itemId, itemDto.getId(), "Идентификаторы не совпадают");
        assertEquals("testItem2", itemDto.getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDto.getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDto.getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    //Получение вещи
    @Test
     void getItemById() {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.create(item.getOwner().getId(), ItemMapper.toItemDto(item));
        ItemDtoBooking itemDtoBooking = itemService.getItem(itemId, userId);

        assertEquals(itemId, itemDtoBooking.getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDtoBooking.getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDtoBooking.getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDtoBooking.getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).findById(itemId);
    }

    //Получение всех вещей пользователя
    @Test
     void getAllItemsByUserId() {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.findByOwnerId(userId, PageRequest.of(0, 20)))
                .thenReturn(Collections.singletonList(item));

        itemService.create(userId, ItemMapper.toItemDto(item));
        final List<ItemDtoBooking> itemDtoBookings = itemService.getAllItemsByUser(userId, 0, 20);

        assertEquals(itemId, itemDtoBookings.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDtoBookings.get(0).getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDtoBookings.get(0).getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDtoBookings.get(0).getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).findByOwnerId(userId, PageRequest.of(0, 20));
    }

    //Поиск вещи
    @Test
     void search() {
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();

        final List<Item> items = new ArrayList<>();
        items.add(item);

        String text = item.getDescription().substring(0, 3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(item.getItemRequest().getId()))
                .thenReturn(Optional.of(item.getItemRequest()));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRepository.search(text, PageRequest.of(0, 20))).thenReturn(items);

        itemService.create(userId, ItemMapper.toItemDto(item));

        final List<ItemDto> itemDtoList = itemService.search(text, 0, 20);

        assertEquals(itemId, itemDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(item.getName(), itemDtoList.get(0).getName(), "Имена не совпадают");
        assertEquals(item.getDescription(), itemDtoList.get(0).getDescription(), "Описания не совпадают");
        assertEquals(item.getAvailable(), itemDtoList.get(0).getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).search(text, PageRequest.of(0, 20));
    }

    //Поиск вещи (пустой)
    @Test
     void searchEmpty() {
        String text = " ";

        final List<ItemDto> itemDtoList = itemService.search(text, 0, 20);

        assertEquals(0, itemDtoList.size(), "Вещь найдена");
    }

    //Создание комментария к вещи
    @Test
     void createCommentForItem() {
        User userWriteComment = item.getItemRequest().getRequestor();

        Comment comment = createValidCommentExample(item, userWriteComment);
        Booking booking = createValidBookingExample(item, userWriteComment);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(userWriteComment.getId())).thenReturn(Optional.of(userWriteComment));

        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);

        when(bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(bookingsList);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);
        CommentDto commentDto = itemService.createComment(userWriteComment.getId(), item.getId(), commentDto1);

        assertEquals("testComment", commentDto.getText());
        assertEquals(userWriteComment.getName(), commentDto.getAuthorName());
        assertEquals(comment.getId(), commentDto.getId());

        verify(commentRepository, times(1)).save(any());
    }

    //Создание вещи с несуществующем пользователем
    @Test
     void createItemUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemService.create(3L, ItemMapper.toItemDto(item)));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение несуществующей вещи
    @Test
     void getUnknownItem() {
        Long userId = item.getOwner().getId();

        Throwable throwable = assertThrows(NotFoundException.class, () -> itemService.getItem(2L, userId));

        assertEquals("Неверный идентификатор вещи", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление несуществующей вещи
    @Test
     void updateUnknownItem() {
        Item item1 = createValidItemExample();
        item1.setName("testItem1");

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemService.update(user.getId(), 3L, ItemMapper.toItemDto(item1)));

        assertEquals("Неверный идентификатор вещи", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление чужой вещи
    @Test
     void updateItemNoOwner() {
        Item item1 = createValidItemExample();
        item1.setName("testItem1");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemService.update(100L, item.getId(), ItemMapper.toItemDto(item1)));

        assertEquals("Нельзя изменить чужую вещь", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Обновление несуществующей вещи
    @Test
     void updateItemNullParam() {
        Long itemId = item.getId();
        Item item1 = createValidItemExample();
        item1.setName("testItem1");
        item1.setName(null);
        item1.setDescription(null);
        item1.setAvailable(null);

        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.update(user.getId(), itemId, ItemMapper.toItemDto(item1));

        assertEquals(itemId, itemDto.getId(), "Идентификаторы не совпадают");
        assertEquals("testItem", itemDto.getName(), "Имена не совпадают");
        assertEquals("itemDescription", itemDto.getDescription(), "Описания не совпадают");
        assertEquals(true, itemDto.getAvailable(), "Статусы не совпадают");

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    //Создание комментария за несуществующего пользователя
    @Test
     void createCommentUnknownUser() {
        User userWriteComment = item.getItemRequest().getRequestor();

        Comment comment = createValidCommentExample(item, userWriteComment);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemService.createComment(3L, item.getId(), commentDto1));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Создание комментария к несуществующей вещи
    @Test
     void createCommentUnknownItem() {
        User userWriteComment = item.getItemRequest().getRequestor();

        Comment comment = createValidCommentExample(item, userWriteComment);

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                itemService.createComment(3L, 100L, commentDto1));

        assertEquals("Неверный идентификатор вещи", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }
}