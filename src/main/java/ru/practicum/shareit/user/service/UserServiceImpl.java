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
        userDto.setId(userId);
        User userOld = userRepository.getUser(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));
        User userNew = UserMapper.toUser(userDto);
        userNew = updateUserData(userNew, userOld);
        return UserMapper.toUserDto(userRepository.update(userNew));
    }

    private User updateUserData(User userNew, User userOld) {
        if (userNew.getName() != null) {
            userOld.setName(userNew.getName());
        }
        if (userNew.getEmail() != null) {
            checkUniqueEmail(userNew);
            userOld.setEmail(userNew.getEmail());
        }

        return userOld;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
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

   /* public void validateEmail(UserDto userDto) {
        if (userRepository.getAll().stream()
                .anyMatch(user -> userDto.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Такой email уже используется");
        }
    }*/
}