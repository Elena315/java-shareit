package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user1;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        User user = userRepository.save(new User(1L, "testUser", "test@yandex.ru"));
        user1 = userRepository.save(new User(2L, "testUser1", "test1@yandex.ru"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "testItemRequest", user,
                LocalDateTime.now()));
        item = itemRepository.save(new Item(1L, "testItem", "testDescription", true, user1,
                itemRequest));
    }

    //Поиск вещи тексту
    @Test
    public void searchItem() {
        final String text = item.getName().substring(0, 3);
        final List<Item> items = itemRepository.search(text, Pageable.unpaged());

        assertEquals(items.size(), 1, "Вещь не найдена");
        assertEquals(items.get(0).getId(), item.getId(), "Идентификаторы не совпадают");
        assertEquals(items.get(0).getName(), item.getName(), "Имена не совпадают");
        assertEquals(items.get(0).getDescription(), item.getDescription(), "Описания не совпадают");
        assertEquals(items.get(0).getItemRequest(), item.getItemRequest(), "Запросы не совпадают");
    }

    //Поиск по запросу
    @Test
    public void getAllItemsRequestId() {
        final List<Item> items = itemRepository.findAllByItemRequestId(itemRequest.getId());

        assertEquals(items.size(), 1, "Вещь не найдена");
        assertEquals(items.get(0).getId(), item.getId(), "Идентификаторы не совпадают");
        assertEquals(items.get(0).getName(), item.getName(), "Имена не совпадают");
        assertEquals(items.get(0).getDescription(), item.getDescription(), "Описания не совпадают");
        assertEquals(items.get(0).getItemRequest(), item.getItemRequest(), "Запросы не совпадают");
    }

    //Поиск вещей по владельцу
    @Test
    public void getAllOwnerId() {
        final List<Item> items = itemRepository.findByOwnerId(user1.getId(), Pageable.unpaged());

        assertEquals(items.size(), 1, "Вещь не найдена");
        assertEquals(items.get(0).getId(), item.getId(), "Идентификаторы не совпадают");
        assertEquals(items.get(0).getName(), item.getName(), "Имена не совпадают");
        assertEquals(items.get(0).getDescription(), item.getDescription(), "Описания не совпадают");
        assertEquals(items.get(0).getItemRequest(), item.getItemRequest(), "Запросы не совпадают");
    }
}