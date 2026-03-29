package io.github.Klodvik1.service;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.event.UserNotificationEvent;
import io.github.Klodvik1.event.UserOperationType;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.producer.UserEventProducer;
import io.github.Klodvik1.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public UserServiceImpl(
            UserRepository userRepository,
            UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = UserMapper.toUser(userRequestDto);
        User savedUser = userRepository.saveAndFlush(user);

        UserNotificationEvent event = new UserNotificationEvent(
                UserOperationType.USER_CREATED,
                savedUser.getEmail()
        );

        userEventProducer.sendUserNotificationEvent(event);

        return UserMapper.toUserResponseDto(savedUser);
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

        User updatedUser = userRepository.saveAndFlush(existingUser);

        return UserMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден."));

        String email = existingUser.getEmail();

        userRepository.delete(existingUser);
        userRepository.flush();

        UserNotificationEvent event = new UserNotificationEvent(
                UserOperationType.USER_DELETED,
                email
        );

        userEventProducer.sendUserNotificationEvent(event);
    }
}
