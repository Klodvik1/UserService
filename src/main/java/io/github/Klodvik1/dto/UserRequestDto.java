package io.github.Klodvik1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Имя пользователя не должно быть пустым.")
        @Size(min = 2, max = 100, message = "Имя пользователя должно содержать от 2 до 100 символов.")
        @Pattern(
                regexp = "^[\\p{L}]+([\\s-][\\p{L}]+)*$",
                message = "Имя пользователя должно содержать только буквы, пробелы и дефисы, без цифр."
        )
        String name,

        @NotBlank(message = "Email пользователя не должен быть пустым.")
        @Email(message = "Email пользователя имеет некорректный формат.")
        @Size(max = 255, message = "Email пользователя не должен быть длиннее 255 символов.")
        String email,

        @NotNull(message = "Возраст пользователя не должен быть null.")
        @Min(value = 18, message = "Возраст пользователя должен быть больше 18.")
        @Max(value = 100, message = "Возраст пользователя должен быть реалистичным.")
        Integer age) {
}
