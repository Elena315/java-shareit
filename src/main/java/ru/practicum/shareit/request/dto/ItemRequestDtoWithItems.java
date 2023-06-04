package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoWithItems {
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
    private List<ItemDto> items;
}