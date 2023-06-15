package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Время начала бронирования не может быть пустым")
    private LocalDateTime start;
    @NotNull(message = "Время конца бронирования не может быть пустым")
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}