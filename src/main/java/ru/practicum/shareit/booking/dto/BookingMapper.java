package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }

    public static Booking fromSimpleToBooking(BookingDtoSimple bookingDtoSimple) {
        return new Booking(bookingDtoSimple.getId(),
                bookingDtoSimple.getStart(),
                bookingDtoSimple.getEnd(),
                null,
                null,
                Status.WAITING);
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(booking.getId(),
                booking.getBooker().getId()
        );
    }
}