package com.musala.drones.domain.ues.handler;

import com.musala.drones.domain.ues.model.dto.ErrorResponse;
import com.musala.drones.domain.ues.model.exception.DroneAppException;
import com.musala.drones.domain.ues.model.exception.InternalDroneAppException;
import com.musala.drones.domain.ues.model.exception.ValidationDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @Value("${server.port}")
    private String serviceCode;

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<ErrorResponse> handleException(Throwable ex, WebRequest req) {
        log.trace("UES caught {} with message {}", ex.getClass().getSimpleName(), ex.getMessage());
        log.error("DIFF APP Unified Exception System handle and resolve error: ", ex);
        DroneAppException e;
        if (ex instanceof DroneAppException) {
            e = (DroneAppException) ex;
        } else if (ex instanceof MethodArgumentNotValidException) {
            e = new ValidationDroneAppException((MethodArgumentNotValidException) ex);
        } else {
            e = new InternalDroneAppException(ex);
        }
        ErrorResponse response = new ErrorResponse(e, serviceCode, req);
        log.trace("Generated response: {}", response);
        return new ResponseEntity<>(response, e.getStatus());
    }

}