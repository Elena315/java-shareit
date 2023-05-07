package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateEmail(userDto);
        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.getUser(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            validateEmail(userDto);
            user.setEmail(userDto.getEmail());
        }

        userRepository.update(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
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

    public void validateEmail(UserDto userDto) {
        if (userRepository.getAll().stream().anyMatch(user1 -> user1.getEmail().equals(userDto.getEmail()))) {
            throw new ValidationException("Такой email уже используется");
        }
    }
}