package io.github.Klodvik1.mapper;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.entity.User;

public final class UserMapper {

    private UserMapper() {

    }

    public static User toUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.name());
        user.setEmail(userRequestDto.email());
        user.setAge(userRequestDto.age());

        return user;
    }

    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
