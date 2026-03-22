package io.github.Klodvik1.service;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.repository.UserRepository;
import io.github.Klodvik1.validation.UserValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        UserRequestDto normalizedUserRequestDto = normalizeUserRequestDto(userRequestDto);
        userValidator.validateEmailUniqueness(normalizedUserRequestDto.email());

        try {
            User user = userMapper.toUser(normalizedUserRequestDto);
            User savedUser = userRepository.save(user);

            return userMapper.toUserResponseDto(savedUser);
        } catch (DataIntegrityViolationException exception) {
            userValidator.validateEmailUniqueness(normalizedUserRequestDto.email());
            throw exception;
        }
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        UserRequestDto normalizedUserRequestDto = normalizeUserRequestDto(userRequestDto);
        userValidator.validateEmailUniquenessForUpdate(id, normalizedUserRequestDto.email());

        try {
            userMapper.applyRequestDto(existingUser, normalizedUserRequestDto);
            User updatedUser = userRepository.save(existingUser);

            return userMapper.toUserResponseDto(updatedUser);
        } catch (DataIntegrityViolationException exception) {
            userValidator.validateEmailUniquenessForUpdate(id, normalizedUserRequestDto.email());
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        userRepository.delete(existingUser);
    }

    private UserRequestDto normalizeUserRequestDto(UserRequestDto userRequestDto) {
        String normalizedName = userRequestDto.name().trim().replaceAll("\\s+", " ");
        String normalizedEmail = userRequestDto.email().trim().toLowerCase(Locale.ROOT);

        return new UserRequestDto(
                normalizedName,
                normalizedEmail,
                userRequestDto.age());
    }
}
