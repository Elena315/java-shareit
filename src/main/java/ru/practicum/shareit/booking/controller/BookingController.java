package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDtoSimple bookingDtoSimple,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос к эндпоинту: '{} {}', Бронирование: ItemId: {}", "POST", "/bookings",
                bookingDtoSimple.getItemId());
        return bookingService.create(bookingDtoSimple, userId);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET booking id={}", id);
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAll(userId, state);
    }


    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос к эндпоинту: '{} {}', Подтверждение бронирование: ID: {}", "PATCH", "/bookings",
                bookingId);
        return bookingService.approve(userId, bookingId, approved);
    }
}
