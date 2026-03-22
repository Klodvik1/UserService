package io.github.Klodvik1.exception;

import io.github.Klodvik1.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            UserNotFoundException exception,
            HttpServletRequest request) {

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                List.of(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateEmailException(
            DuplicateEmailException exception,
            HttpServletRequest request) {

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                List.of(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации тела запроса.",
                details,
                request.getRequestURI());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        List<String> details = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации параметров запроса.",
                details,
                request.getRequestURI());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Тело запроса отсутствует или имеет некорректный формат.",
                List.of(),
                request.getRequestURI());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        String message = resolveDataIntegrityViolationMessage(exception);

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.CONFLICT,
                message,
                List.of(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception exception,
            HttpServletRequest request) {

        LOGGER.error("Unhandled exception for path {}", request.getRequestURI(), exception);

        ErrorResponseDto response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера.",
                List.of(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ErrorResponseDto buildErrorResponse(
            HttpStatus status,
            String message,
            List<String> details,
            String path) {

        return new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details,
                path);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String resolveDataIntegrityViolationMessage(DataIntegrityViolationException exception) {
        exception.getMostSpecificCause();
        String rootCauseMessage = exception.getMostSpecificCause().getMessage().toLowerCase();

        if (rootCauseMessage.contains("email") || rootCauseMessage.contains("users_email_key")) {
            return "Пользователь с таким email уже существует.";
        }

        return "Нарушено ограничение целостности данных.";
    }
}
