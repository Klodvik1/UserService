package io.github.Klodvik1.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt) {
}
