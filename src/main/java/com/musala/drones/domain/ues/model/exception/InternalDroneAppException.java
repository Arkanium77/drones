package com.musala.drones.domain.ues.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalDroneAppException extends DroneAppException {

    public InternalDroneAppException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalDroneAppException(HttpStatus status, String message) {
        super(status, message);
    }

    public InternalDroneAppException(Throwable ex) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, throwableToString(ex));
    }

    public static InternalDroneAppException of(String message) {
        return new InternalDroneAppException(message);
    }
}