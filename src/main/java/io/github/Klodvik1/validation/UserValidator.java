package io.github.Klodvik1.validation;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class UserValidator {
    private final Validator validator;

    public UserValidator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public void validateUserRequestDto(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new ValidationException("Данные пользователя не должны быть null.");
        }

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));

            throw new ValidationException(errorMessage);
        }
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id должен быть положительным числом.");
        }
    }
}