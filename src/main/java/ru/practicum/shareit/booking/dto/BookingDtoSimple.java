package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoSimple {
    private Long id;
    @NotNull(message = "Время начала бронирования не может быть пустым")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Время конца бронирования не может быть пустым")
    @FutureOrPresent
    private LocalDateTime end;
    private Long itemId;
}