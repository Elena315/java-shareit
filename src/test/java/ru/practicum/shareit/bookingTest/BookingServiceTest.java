package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.enums.Status.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockitoSettings(strictness = Strictness.LENIENT)
@Profile("test")
public class BookingServiceTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private Booking booking;
    private User booker;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        booking = createBookingExample();
    }

    private Booking createBookingExample() {
        User owner = new User(1L, "testOwner", "testOwner@yandex.ru");
        booker = new User(2L, "testBooker", "testBooker@yandex.ru");
        Item item = new Item(1L, "testItem", "testDescription", true, owner, null);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        booking = new Booking(1L, start, end, item, booker, APPROVED);
        return booking;
    }

    private static BookingDtoSimple toBookingDtoSimple(Booking booking) {
        return new BookingDtoSimple(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    //Создание брони
    @Test
    public void createValidBooking() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto bookingDto = bookingService.create(toBookingDtoSimple(booking), bookerId);

        assertEquals(booking.getId(), bookingDto.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    //Получение брони
    @Test
    public void getBooking() {
        Long bookerId = booking.getBooker().getId();
        Long bookingId = booking.getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBooking(bookingId, bookerId);

        assertEquals(booking.getId(), bookingDto.getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName(), "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDto.getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    //Получение всех броней
    @Test
    public void getAllBookings() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(bookerId, PageRequest.of(0, 20, Sort.by("start")
                .descending()))).thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "ALL", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findByBookerId(bookerId,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Получение всех броней со статусом CURRENT
    @Test
    public void getAllBookingCurrent() {
        Long bookerId = booker.getId();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.findCurrentBookingsByBookerId(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "CURRENT", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findCurrentBookingsByBookerId(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех броней со статусом PAST
    @Test
    public void getAllBookingPast() {
        Long bookerId = booker.getId();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.findBookingsByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "PAST", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndEndIsBefore(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех броней со статусом FUTURE
    @Test
    public void getAllBookingFuture() {
        Long bookerId = booker.getId();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        final List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "FUTURE", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");
        assertEquals(booking.getStatus(), bookingDtoList.get(0).getStatus(), "Статусы не совпадают");

        verify(bookingRepository, times(1)).findByBookerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех броней со статусом WAITING
    @Test
    public void getAllBookingWaiting() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        booking.setStatus(WAITING);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdAndStatus(bookerId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "WAITING", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStatus(bookerId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Получение всех броней со статусом REJECTED
    @Test
    public void getAllBookingsRejected() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        booking.setStatus(REJECTED);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdAndStatus(bookerId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAll(bookerId, "REJECTED", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStatus(bookerId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Получение всех бронирований пользователя
    @Test
    public void getAllUserBookings() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerId(itemUserId,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "ALL", 0, 20);


        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerId(itemUserId,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Получение всех бронирований пользователя со статусом CURRENT
    @Test
    public void getAllUserBookingsCurrent() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentBookingsByItemOwnerId(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "CURRENT", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findCurrentBookingsByItemOwnerId(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех бронирований пользователя со статусом PAST
    @Test
    public void getAllUserBookingsPast() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "PAST", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findBookingsByItemOwnerIdAndEndIsBefore(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех бронирований пользователя со статусом FUTURE
    @Test
    public void getAllUserBookingsFuture() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.searchBookingByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "FUTURE", 0, 20);

        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).searchBookingByItemOwnerIdAndStartIsAfter(anyLong(),
                any(LocalDateTime.class), any());
    }

    //Получение всех бронирований пользователя со статусом WAITING
    @Test
    public void getAllUserBookingsWaiting() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        booking.setStatus(WAITING);

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatus(itemUserId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "WAITING", 0, 20);


        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findBookingsByItemOwnerIdAndStatus(itemUserId, WAITING,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Получение всех бронирований пользователя со статусом REJECTED
    @Test
    public void getAllUserBookingsRejected() {
        User booker = booking.getBooker();
        Long itemUserId = booking.getItem().getOwner().getId();

        booking.setStatus(REJECTED);

        when(userRepository.findById(itemUserId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatus(itemUserId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending())))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookingDtoList = bookingService.getAllBookingByOwner(itemUserId, "REJECTED", 0, 20);


        assertEquals(bookingDtoList.size(), 1, "Бронь отсутствует");
        assertEquals(booking.getId(), bookingDtoList.get(0).getId(), "Идентификаторы не совпадают");
        assertEquals(booking.getStart(), bookingDtoList.get(0).getStart(), "Время начала не совпадает");
        assertEquals(booking.getEnd(), bookingDtoList.get(0).getEnd(), "Время конца не совпадает");
        assertEquals(booking.getBooker().getName(), bookingDtoList.get(0).getBooker().getName(),
                "Имена не совпадают");

        verify(bookingRepository, times(1)).findBookingsByItemOwnerIdAndStatus(itemUserId, REJECTED,
                PageRequest.of(0, 20, Sort.by("start").descending()));
    }

    //Подтверждение брони не владельцем вещи
    @Test
    public void approveBookingNoOwnerItem() {
        Long bookingId = booking.getId();

        booking.setStatus(WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.approve(booker.getId(), bookingId, true));

        assertEquals("Подтвердить бронирование может только владелец вещи", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    //Подтверждение подтвержденной брони
    @Test
    public void approveBookingApproved() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approve(itemUserId,
                bookingId, true));
        assertNotNull(throwable.getMessage());

        assertEquals("Бронирование уже подтверждено", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    //Подтверждение брони, где статус неизвестен
    @Test
    public void approveBookingApproveNull() {
        Long bookingId = booking.getId();
        Long itemUserId = booking.getItem().getOwner().getId();

        booking.setStatus(WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(ValidationException.class, () -> bookingService.approve(itemUserId,
                bookingId, null));
        assertNotNull(throwable.getMessage());

        assertEquals("Необходимо подтвердить бронирование", throwable.getMessage(),
                "Текст ошибки валидации разный");

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    //Создание брони c неизвестным пользователем
    @Test
    public void createBookingUnknownUser() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Неверный идентификатор пользователя"));

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.create(toBookingDtoSimple(booking), 3L));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Неверный идентификатор пользователя");

        verify(userRepository, times(1)).findById(anyLong());
    }

    //Нельзя создать бронь
    @Test
    public void createBookingFalseItem() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();
        Long itemId = booking.getItem().getId();
        Item item = booking.getItem();
        item.setAvailable(false);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable throwable = assertThrows(AvailableException.class, () ->
                bookingService.create(toBookingDtoSimple(booking), bookerId));

        assertEquals("Эта вещь недоступна для аренды", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Бронирование собственной вещи
    @Test
    public void createBookingOwnItem() {
        Item item = booking.getItem();
        Long ownerId = item.getOwner().getId();
        User owner = item.getOwner();
        Long itemId = booking.getItem().getId();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.create(toBookingDtoSimple(booking), ownerId));

        assertEquals("Владелец вещи не может забронировать свою вещь", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение брони с неизвестной бронью
    @Test
    public void getUnknownBooking() {
        Long bookingId = booking.getId();

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(bookingId, 3L));

        assertEquals("Неверный идентификатор брони", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение всех бронирований с несуществующим пользователем
    @Test
    public void getAllBookingsUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () ->
                bookingService.getAll(3L, "ALL", 0, 20));

        assertEquals("Неверный идентификатор пользователя", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Время конца раньше времени начала бронирования
    @Test
    public void createBookingWithInvalidLocalDateTimeEnd() {
        LocalDateTime endError = booking.getEnd().minusDays(20);
        booking.setEnd(endError);

        Throwable throwable = assertThrows(ValidationException.class, () ->
                bookingService.create(toBookingDtoSimple(booking), booking.getBooker().getId()));
        assertEquals("Время окончания не может быть больше времени начала", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение всех броней со статусом UNSUPPORTED_STATUS
    @Test
    public void getAllBookingUnsupportedStatus() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        Throwable throwable = assertThrows(ValidationException.class, () ->
                bookingService.getAll(bookerId, "APPROVED", 0, 20));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }

    //Получение всех бронирований пользователя со статусом UNSUPPORTED_STATUS
    @Test
    public void getAllUserBookingUnsupportedStatus() {
        Long bookerId = booking.getBooker().getId();
        User booker = booking.getBooker();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        Throwable throwable = assertThrows(ValidationException.class, () ->
                bookingService.getAllBookingByOwner(bookerId, "APPROVED", 0, 20));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", throwable.getMessage(),
                "Текст ошибки валидации разный");
    }
}