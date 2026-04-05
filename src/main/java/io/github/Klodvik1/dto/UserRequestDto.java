package io.github.Klodvik1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "UserRequestDto", description = "DTO для создания или обновления пользователя")
public record UserRequestDto(
        @Schema(description = "Имя пользователя", example = "Denis")
        @NotBlank(message = "Имя пользователя не должно быть пустым.")
        @Size(min = 2, max = 100, message = "Имя пользователя должно содержать от 2 до 100 символов.")
        @Pattern(
                regexp = "^[\\p{L}]+([\\s-][\\p{L}]+)*$",
                message = "Имя пользователя не должно содержать цифр."
        )
        String name,

        @Schema(description = "Email пользователя", example = "denis@gmail.com")
        @NotBlank(message = "Email пользователя не должен быть пустым.")
        @Email(message = "Email пользователя имеет некорректный формат.")
        @Size(max = 255, message = "Email пользователя не должен быть длиннее 255 символов.")
        String email,

        @Schema(description = "Возраст пользователя", example = "23")
        @NotNull(message = "Возраст пользователя не должен быть null.")
        @Min(value = 18, message = "Возраст пользователя должен быть не младше 18.")
        @Max(value = 100, message = "Возраст пользователя должен быть реалистичным.")
        Integer age) {
}
