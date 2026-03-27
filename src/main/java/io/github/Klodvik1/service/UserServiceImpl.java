package io.github.Klodvik1.service;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        try {
            User user = UserMapper.toUser(userRequestDto);
            User savedUser = userRepository.saveAndFlush(user);

            return UserMapper.toUserResponseDto(savedUser);
        } catch (DataIntegrityViolationException exception) {
            throw exception;
        }
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toUserResponseDto);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        existingUser.setName(userRequestDto.name());
        existingUser.setEmail(userRequestDto.email());
        existingUser.setAge(userRequestDto.age());

        try {
            User updatedUser = userRepository.saveAndFlush(existingUser);

            return UserMapper.toUserResponseDto(updatedUser);
        } catch (DataIntegrityViolationException exception) {
            throw exception;
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
