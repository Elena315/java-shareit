package ru.practicum.shareit.bookingTest.dtoTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;
@SpringBootTest
public class ToBookingTest {

    private BookingDto createBookingDtoExample() {
        User owner = new User(1L, "testOwner", "test@yandex.ru");
        User booker = new User(2L, "testOwnerBooker", "test1@yandex.ru");
        Item item = new Item(1L, "testItem", "testDescriptionItem", true, owner,
                null);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        return new BookingDto(1L, start, end, item, booker, APPROVED);
    }

    @Test
    public void toBookingDtoForItem() {
        BookingDto bookingDto = createBookingDtoExample();
        Booking booking = BookingMapper.toBooking(bookingDto);

        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }
}