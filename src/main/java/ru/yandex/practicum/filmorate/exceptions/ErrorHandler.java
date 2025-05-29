package ru.yandex.practicum.filmorate.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final String ERROR_KEY = "error";
    private static final String VALIDATION_ERROR_MESSAGE = "Ошибка валидации";
    private static final String ENTITY_NOT_FOUND_MESSAGE = "Сущность не найдена";
    private static final String DATA_NOT_FOUND_MESSAGE = "Запрашиваемые данные не найдены";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Внутренняя ошибка сервера";

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.error("{}: {}", VALIDATION_ERROR_MESSAGE, ex.getMessage());
        return createErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        log.error("{}: {}", VALIDATION_ERROR_MESSAGE, errors);
        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        log.error("{}: {}", VALIDATION_ERROR_MESSAGE, errors);
        return errors;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("{}: {}", ENTITY_NOT_FOUND_MESSAGE, e.getMessage());
        return createErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        log.error("{}: {}", DATA_NOT_FOUND_MESSAGE, e.getMessage());
        return createErrorResponse(DATA_NOT_FOUND_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerError(Exception e) {
        log.error("{}: {}", INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage());
        return createErrorResponse(INTERNAL_SERVER_ERROR_MESSAGE);
    }

    private Map<String, String> createErrorResponse(String errorMessage) {
        return Map.of(ERROR_KEY, errorMessage);
    }
}