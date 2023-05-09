package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkUniqueEmail(user);
        userDto.setId(userRepository.create(user).getId());
        return userDto;
    }

    private void checkUniqueEmail(User user) {
        List<String> userEmails = userRepository.getAllUsers().values().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if (userEmails.contains(user.getEmail())) {
            throw new ValidationException(
                    String.format("Пользователь с email = '%s' уже существует", user.getEmail()));
        }
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.getUser(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));


        String name = userDto.getName();
        String email = userDto.getEmail();
        user.setName(name != null && !name.isBlank() ? name : user.getName());


        if (email != null && !email.isBlank()) {
            boolean emailExist = userRepository.getAll().stream()
                    .filter(usery -> !usery.getId().equals(userId))
                    .anyMatch(usery -> email.equals(usery.getEmail()));
            if (emailExist) {
                throw new IllegalArgumentException(String.format("Error with email: %s, it already exist!", email));
            }
            user.setEmail(email);
        }
        userRepository.update(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> list = new ArrayList<>();
        for (User user : userRepository.getAll()) {
            UserDto userDto = UserMapper.toUserDto(user);
            list.add(userDto);
        }
        return list;
    }

    @Override
    public void delete(Long userId) {
        getUser(userId);
        userRepository.delete(userId);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));
        return UserMapper.toUserDto(user);
    }
}