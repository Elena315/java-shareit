package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class CommentRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private Item item;
    private Comment comment;

    @BeforeEach
    public void beforeEach() {
        User user = userRepository.save(new User(1L, "testUser", "test@yandex.ru"));
        item = itemRepository.save(new Item(1L, "testItem", "testItemRequest",
                true, user, null));
        comment = commentRepository.save(new Comment(1L, "testComment", item, user, LocalDateTime.now()));
    }

    //Получение комментариев под вещью
    @Test
    public void getAllCommentsByItemIdTest() {
        final List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertEquals(comments.size(), 1, "Комментарий отсутствует");
        assertEquals(comment.getId(), comments.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(comment.getText(), comments.get(0).getText(), "Тексты не совпадают");
    }
}