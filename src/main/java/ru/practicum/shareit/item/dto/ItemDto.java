package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    @NotNull(message = "Имя вещи не может быть пустым")
    @NotEmpty(message = "Описание вещи не может быть пустым")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым")
    @NotNull(message = "Описание вещи не может быть пустым")
    @NotEmpty(message = "Описание вещи не может быть пустым")
    private String description;
    @NotNull(message = "Статус не может быть null")
    private Boolean available;
    private Long requestId;
}