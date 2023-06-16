package ru.practicum.shareit.bookingTest;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.enums.Status.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Profile("test")
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        booking = createBookingExample();
    }

    private Booking createBookingExample() {
        User owner = new User(1L, "testOwner", "testOwner@yandex.ru");
        User booker = new User(2L, "testBooker", "testBooker@yandex.ru");
        Item item = new Item(1L, "testItem", "testDescription", true, owner, null);

        LocalDateTime start = LocalDateTime.parse("2022-09-10T10:42");
        LocalDateTime end = LocalDateTime.parse("2022-09-12T10:42");
        return new Booking(1L, start, end, item, booker, APPROVED);
    }

    private static BookingDtoSimple toBookingDtoSimple(Booking booking) {
        return new BookingDtoSimple(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    //Создание брони
    @Test
    void createValidBooking() throws Exception {
        Long bookerId = booking.getBooker().getId();

        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        BookingDtoSimple bookingDtoSimple = toBookingDtoSimple(booking);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.create(bookingDtoSimple, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoSimple))
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        " \"item\": {\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"testDescription\", \"available\": true," +
                        " \"owner\": {\"id\": 1,\"name\": \"testOwner\",\"email\": \"testOwner@yandex.ru\"}," +
                        " \"itemRequest\": null}," +
                        " \"booker\": {\"id\": 2,\"name\": \"testBooker\",\"email\": \"testBooker@yandex.ru\"}}"));

        verify(bookingService, times(1)).create(bookingDtoSimple, bookerId);
    }

    //Получение брони
    @Test
    void getBooking() throws Exception {
        Long bookerId = booking.getBooker().getId();
        Long bookingId = booking.getId();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.getBooking(bookingId, bookerId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        " \"item\": {\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"testDescription\", \"available\": true," +
                        " \"owner\": {\"id\": 1,\"name\": \"testOwner\",\"email\": \"testOwner@yandex.ru\"}," +
                        " \"itemRequest\": null}," +
                        " \"booker\": {\"id\": 2,\"name\": \"testBooker\",\"email\": \"testBooker@yandex.ru\"}}"));

        verify(bookingService, times(1)).getBooking(bookingId, bookerId);
    }

    //Получение всех броней
    @Test
    public void getAllBookings() throws Exception {
        List<BookingDto> bookingDtoList = new ArrayList<>();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        bookingDtoList.add(bookingDto);

        when(bookingService.getAll(bookingDto.getBooker().getId(), "ALL", 0, 20))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        " \"item\": {\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"testDescription\", \"available\": true," +
                        " \"owner\": {\"id\": 1,\"name\": \"testOwner\",\"email\": \"testOwner@yandex.ru\"}," +
                        " \"itemRequest\": null}," +
                        " \"booker\": {\"id\": 2,\"name\": \"testBooker\",\"email\": \"testBooker@yandex.ru\"}}]"));

        verify(bookingService, times(1))
                .getAll(bookingDto.getBooker().getId(), "ALL", 0, 20);
    }

    //Получение всех бронирований пользователя
    @Test
    void getAllUserBookings() throws Exception {
        Long itemUserId = booking.getItem().getOwner().getId();

        List<BookingDto> bookingDtoList = new ArrayList<>();

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        bookingDtoList.add(bookingDto);

        when(bookingService.getAllBookingByOwner(itemUserId, "ALL", 0, 20)).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", bookingDto.getItem().getOwner().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        " \"item\": {\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"testDescription\", \"available\": true," +
                        " \"owner\": {\"id\": 1,\"name\": \"testOwner\",\"email\": \"testOwner@yandex.ru\"}," +
                        " \"itemRequest\": null}," +
                        " \"booker\": {\"id\": 2,\"name\": \"testBooker\",\"email\": \"testBooker@yandex.ru\"}}]"));

        verify(bookingService, times(1)).getAllBookingByOwner(itemUserId, "ALL", 0,
                20);
    }

    //Подтверждение брони
    @Test
    void approve() throws Exception {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();

        booking.setStatus(WAITING);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(bookingService.approve(itemUserId, bookingId, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        " \"item\": {\"id\": 1,\"name\": \"testItem\"," +
                        " \"description\": \"testDescription\", \"available\": true," +
                        " \"owner\": {\"id\": 1,\"name\": \"testOwner\",\"email\": \"testOwner@yandex.ru\"}," +
                        " \"itemRequest\": null}," +
                        " \"booker\": {\"id\": 2,\"name\": \"testBooker\",\"email\": \"testBooker@yandex.ru\"}}"));
        verify(bookingService, times(1)).approve(itemUserId, bookingId, true);
    }
}