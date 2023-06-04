package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoSimple bookingDtoSimple, long userId);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getAll(long userId, String state);

    List<BookingDto> getAllBookingByOwner(long userId, String state);

    BookingDto approve(long userId, long bookingId, Boolean approved);

}