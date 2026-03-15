package io.github.Klodvik1.service;

import io.github.Klodvik1.dao.UserDao;
import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto requestDto;
    private User user;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new UserRequestDto(
                "Denis",
                "denis@gmail.com",
                23
        );

        user = new User();
        user.setId(1L);
        user.setName("Denis");
        user.setEmail("denis@gmail.com");
        user.setAge(23);
        user.setCreatedAt(LocalDateTime.now());

        responseDto = new UserResponseDto(
                1L,
                "Denis",
                "denis@gmail.com",
                23,
                user.getCreatedAt()
        );
    }

    @Test
    void createUser_ShouldReturnResponseDto_WhenRequestIsValid() {
        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(userDao.create(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(requestDto);

        verify(userValidator).validateUserRequestDto(requestDto);
        verify(userMapper).toUser(requestDto);
        verify(userDao).create(user);
        verify(userMapper).toUserResponseDto(user);

        assertEquals(responseDto, result);
    }

    @Test
    void getUserById_ShouldReturnResponseDto_WhenUserExists() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(1L);

        verify(userValidator).validateId(1L);
        verify(userDao).findById(1L);
        verify(userMapper).toUserResponseDto(user);

        assertEquals(responseDto, result);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(1L)
        );

        verify(userValidator).validateId(1L);
        verify(userDao).findById(1L);
    }

    @Test
    void deleteUserById_ShouldDeleteUser_WhenUserExists() {
        when(userDao.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUserById(1L));

        verify(userValidator).validateId(1L);
        verify(userDao).deleteById(1L);
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userDao.deleteById(1L)).thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUserById(1L)
        );

        verify(userValidator).validateId(1L);
        verify(userDao).deleteById(1L);
    }
}