package io.github.Klodvik1.mapper;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserRequestDto userRequestDto) {
        User user = new User();
        applyRequestDto(user, userRequestDto);

        return user;
    }

    public void applyRequestDto(User user, UserRequestDto userRequestDto) {
        user.setName(userRequestDto.name());
        user.setEmail(userRequestDto.email());
        user.setAge(userRequestDto.age());
    }

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt());
    }
}
