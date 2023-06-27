package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.Status.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Profile("test")
public class BookingServiceWithoutMockTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final User owner = new User(1L, "testOwner", "testOwner@yandex.ru");
    private final User booker = new User(2L, "testBooker", "testBooker@yandex.ru");
    private final Item item = new Item(1L, "testItem", "testDescription", true, owner,
            null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
            item, booker, WAITING);

    private static BookingDtoSimple toBookingDtoSimple(Booking booking) {
        return new BookingDtoSimple(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    //Создание брони
    @Test
    public void createValidBooking() {
        UserDto ownerDto = userService.create(UserMapper.toUserDto(owner));
        UserDto bookerDto = userService.create(UserMapper.toUserDto(booker));

        itemService.create(ownerDto.getId(), ItemMapper.toItemDto(item));

        BookingDto bookingDto = bookingService.create(toBookingDtoSimple(booking), bookerDto.getId());

        assertEquals(booking.getId(), bookingDto.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "Статусы не совпадают");
    }

    //Получение брони
    @Test
    public void getBooking() {
        UserDto ownerDto = userService.create(UserMapper.toUserDto(owner));
        UserDto bookerDto = userService.create(UserMapper.toUserDto(booker));

        itemService.create(ownerDto.getId(), ItemMapper.toItemDto(item));

        BookingDto bookingDto = bookingService.create(toBookingDtoSimple(booking), bookerDto.getId());
        BookingDto bookingDto1 = bookingService.getBooking(bookingDto.getId(), bookerDto.getId());

        assertEquals(booking.getId(), bookingDto1.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getBooker().getName(), bookingDto1.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto1.getStatus(), "Статусы не совпадают");
    }
}