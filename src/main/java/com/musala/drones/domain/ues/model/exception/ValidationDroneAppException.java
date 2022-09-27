package com.musala.drones.domain.ues.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

@Getter
public class ValidationDroneAppException extends DroneAppException {

    public ValidationDroneAppException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ValidationDroneAppException(MethodArgumentNotValidException exception) {
        super(HttpStatus.BAD_REQUEST, transformValidatorMessage(exception));
    }

    public static ValidationDroneAppException of(String message) {
        return new ValidationDroneAppException(message);
    }

    protected static String transformValidatorMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new StringBuilder()
                        .append("Field '")
                        .append(fieldError.getField())
                        .append("' - ")
                        .append(fieldError.getDefaultMessage()))
                .map(StringBuilder::toString)
                .collect(Collectors.joining("; "));
    }
}