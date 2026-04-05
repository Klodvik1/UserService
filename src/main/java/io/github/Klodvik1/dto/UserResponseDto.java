package io.github.Klodvik1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(collectionRelation = "users", itemRelation = "user")
@Schema(name = "UserResponseDto", description = "DTO ответа с данными пользователя")
public record UserResponseDto(
        @Schema(description = "Идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "Denis")
        String name,

        @Schema(description = "Email пользователя", example = "denis@gmail.com")
        String email,

        @Schema(description = "Возраст пользователя", example = "23")
        Integer age,

        @Schema(description = "Дата и время создания пользователя", example = "21.03.2026 12:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime createdAt) {
}
