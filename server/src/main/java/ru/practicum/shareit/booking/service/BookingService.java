package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoSimple bookingDtoSimple, long userId);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getAll(long userId, String state, int from, int size);

    List<BookingDto> getAllBookingByOwner(long userId, String state, int from, int size);

    BookingDto approve(long userId, long bookingId, Boolean approved);

}