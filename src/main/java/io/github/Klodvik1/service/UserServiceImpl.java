package io.github.Klodvik1.service;

import io.github.Klodvik1.dao.UserDao;
import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.validation.UserValidator;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper, UserValidator userValidator) {
        this.userDao = userDao;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        userValidator.validateUserRequestDto(userRequestDto);

        User user = userMapper.toUser(userRequestDto);
        User savedUser = userDao.create(user);

        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        userValidator.validateId(id);

        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userDao.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        userValidator.validateId(id);
        userValidator.validateUserRequestDto(userRequestDto);

        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        existingUser.setName(userRequestDto.name());
        existingUser.setEmail(userRequestDto.email());
        existingUser.setAge(userRequestDto.age());

        User updatedUser = userDao.update(existingUser);

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userValidator.validateId(id);

        boolean isDeleted = userDao.deleteById(id);

        if (!isDeleted) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден.");
        }
    }
}