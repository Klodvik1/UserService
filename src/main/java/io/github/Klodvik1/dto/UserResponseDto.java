package io.github.Klodvik1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Integer age,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime createdAt) {
}
