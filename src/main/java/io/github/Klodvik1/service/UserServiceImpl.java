package io.github.Klodvik1.service;

import io.github.Klodvik1.dao.UserDao;
import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.exception.ValidationException;
import io.github.Klodvik1.mapper.UserMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final Validator validator;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validateIdNotRequired(userRequestDto);

        User user = userMapper.toUser(userRequestDto);
        User savedUser = userDao.create(user);

        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        validateId(id);

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
        validateId(id);
        validateIdNotRequired(userRequestDto);

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
        validateId(id);

        boolean isDeleted = userDao.deleteById(id);

        if (!isDeleted) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден.");
        }
    }

    private void validateIdNotRequired(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new ValidationException("Данные пользователя не должны быть null.");
        }

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));

            throw new ValidationException(errorMessage);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id должен быть положительным числом.");
        }
    }
}
