package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //Создание брони
    @Override
    public BookingDto create(BookingDtoSimple bookingDtoSimple, long userId) {
        if (bookingDtoSimple.getEnd().isBefore(bookingDtoSimple.getStart())) {
            throw new ValidationException("Время окончания не может быть больше времени начала");
        }
        if (bookingDtoSimple.getEnd().equals(bookingDtoSimple.getStart())) {
            throw new ValidationException("Время окончания не может быть равно времени начала");
        }
        Booking booking = BookingMapper.fromSimpleToBooking(bookingDtoSimple);

        booking.setBooker(userRepository.findById(userId).orElseThrow());

        Item item = itemRepository.findById(bookingDtoSimple.getItemId())
                .orElseThrow(() -> new NotFoundException("Неверный идентификатор вещи"));

        if (!item.getAvailable()) {
            throw new AvailableException("Эта вещь недоступна для аренды");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может забронировать свою вещь");
        }

        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    //Получение брони
    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Неверный идентификатор брони"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Неверный идентификатор пользователя");
        }

        return BookingMapper.toBookingDto(booking);
    }

    //Получение всех бронирований
    public List<BookingDto> getAll(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));

        List<Booking> bookings;

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    //Получение всех бронирований пользователя
    @Override
    public List<BookingDto> getAllBookingByOwner(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(userId, sortByStartDesc);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING, sortByStartDesc);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED, sortByStartDesc);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    //Подтверждение брони
    @Override
    public BookingDto approve(long userId, long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Неверный идентификатор брони"));

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        if (bookingDto.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        if (bookingDto.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved == null) {
            throw new ValidationException("Необходимо подтвердить бронирование");
        } else if (approved) {
            bookingDto.setStatus(Status.APPROVED);
        } else {
            bookingDto.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto)));
    }
}