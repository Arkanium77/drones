package com.musala.drones.domain.ues.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class DroneAppException extends RuntimeException {
    private final HttpStatus status;

    public DroneAppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    protected static String throwableToString(Throwable t) {
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }
}
