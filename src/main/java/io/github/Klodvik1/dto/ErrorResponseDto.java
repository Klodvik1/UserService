package io.github.Klodvik1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details,
        String path) {
}
