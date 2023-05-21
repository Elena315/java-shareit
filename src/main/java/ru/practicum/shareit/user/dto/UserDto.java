package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Некорректно заполнен email пользователя")
    @NotBlank(message = "Некорректно заполнен email пользователя")
    private String email;
}