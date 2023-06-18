package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingItemRequestDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingItemRequestDto bookingItemRequestDto) {
        if (bookingItemRequestDto.getEnd().isBefore(bookingItemRequestDto.getStart())) {
            throw new ValidationException("Время окончания не может быть больше времени начала");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Бронирование: ItemId: {}", "POST", "/bookings",
                bookingItemRequestDto.getItemId());
        return bookingClient.create(userId, bookingItemRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        log.info("GET booking id={}", id);
        return bookingClient.getBooking(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "20") @Positive int size) {

        return bookingClient.getAll(userId, state, from, size);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "20") @Positive int size) {

        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId, @RequestParam Boolean approved) {
        log.info("Получен запрос к эндпоинту: '{} {}', Подтверждение бронирование: ID: {}", "PATCH", "/bookings",
                bookingId);
        return bookingClient.approve(userId, bookingId, approved);
    }
}