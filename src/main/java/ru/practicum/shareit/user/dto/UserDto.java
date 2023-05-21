package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Не является почтовым адресом")
    @NotBlank(message = "Некорректно заполнен email пользователя")
    private String email;
}