package com.musala.drones.domain.ues.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundDroneAppException extends DroneAppException {

    public ResourceNotFoundDroneAppException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public static ResourceNotFoundDroneAppException of(String message) {
        return new ResourceNotFoundDroneAppException(message);
    }
}