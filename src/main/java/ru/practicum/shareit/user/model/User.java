package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @Email(message = "Не является адресом.")
    @NotBlank(message = "Почтовый адрес не может быть пустым.")
    private String email;
}