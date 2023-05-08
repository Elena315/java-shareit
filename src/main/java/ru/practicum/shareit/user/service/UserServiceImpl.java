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
        validateEmail(userDto);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

     @Override
     public UserDto update(Long userId, UserDto userDto) {
         User user = userRepository.getUser(userId).orElseThrow(() ->
                 new NotFoundException("Неверный идентификатор пользователя"));

                 if (userDto.getName() != null) {
                     user.setName(userDto.getName());
                 } else user.setName(user.getName());

                 if (userDto.getEmail() != null) {
                     if (userRepository.getAll().stream()
                             .filter(user1 -> !user1.getId().equals(userDto.getId()))
                             .anyMatch(user1 -> userDto.getEmail().equals(user1.getEmail()))) {
                         throw new ValidationException("Такой email уже используется");
                     }
                     user.setEmail(userDto.getEmail());
                 }
                 userRepository.update(user);
                 return UserMapper.toUserDto(user);
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

    public void validateEmail(UserDto userDto) {
        if (userRepository.getAll().stream()
                .filter(user -> !user.getId().equals(userDto.getId()))
                .anyMatch(user -> userDto.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Такой email уже используется");
        }
    }
}