package ru.practicum.shareit.bookingTest.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;
@SpringBootTest
public class BookingDtoForItemTest {
    private final BookingDtoForItem bookingDtoForItem = new BookingDtoForItem(1L, 2L);
    private Booking booking;

    private Booking createBooking() {
        User owner = new User(1L, "testOwner", "test@yandex.ru");
        User booker = new User(2L, "testOwnerBooker", "test1@yandex.ru");
        Item item = new Item(1L, "testItem", "testDescriptionItem", true, owner,
                null);
        LocalDateTime start = LocalDateTime.parse("2022-09-10T10:42");
        LocalDateTime end = LocalDateTime.parse("2022-09-12T10:42");
        booking = new Booking(1L, start, end, item, booker, APPROVED);
        return booking;
    }

    @Test
    void toBookingDtoForItem() {
        booking = createBooking();
        BookingDtoForItem bookingDtoForItem1 = BookingMapper.toBookingDtoForItem(booking);

        assertEquals(bookingDtoForItem1.getBookerId(), 2L);
    }

    @Test
    void getId() {
        Long id = bookingDtoForItem.getId();

        assertEquals(id, 1);
    }

    @Test
    void setId() {
        bookingDtoForItem.setId(5L);

        assertEquals(bookingDtoForItem.getId(), 5);
    }

    @Test
    void getBookerId() {
        Long bookerId = bookingDtoForItem.getBookerId();

        assertEquals(bookerId, 2);
    }

    @Test
    void setBookerId() {
        bookingDtoForItem.setBookerId(5L);

        assertEquals(bookingDtoForItem.getBookerId(), 5);
    }
}