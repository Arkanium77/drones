package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.enums.DroneState;
import com.musala.drones.domain.ues.model.exception.ValidationDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    public void checkDroneCanBeInLoadingState(DroneEntity drone) {
        if (drone.getState().equals(DroneState.LOADING)
                && drone.getBatteryCapacity() < 25) {
            throw new ValidationDroneAppException("The drone cannot be in the LOADING state when the charge level is less than 25%");
        }
    }

    public void checkState(DroneState current, DroneState next) {
        ValidationDroneAppException e = new ValidationDroneAppException("Invalid state change operation. " +
                "Current state is " + current + ", but next state is " + next);
        if (current.equals(next)) {
            return;
        }
        switch (current) {
            case IDLE: {
                if (!DroneState.LOADING.equals(next)) {
                    throw e;
                }
                return;
            }
            case LOADING: {
                if (!(DroneState.IDLE.equals(next) || DroneState.LOADED.equals(next))) {
                    throw e;
                }
                return;
            }
            case LOADED: {
                if (DroneState.DELIVERED.equals(next) || DroneState.RETURNING.equals(next)) {
                    throw e;
                }
                return;
            }
            case DELIVERING: {
                if (!(DroneState.DELIVERED.equals(next) || DroneState.RETURNING.equals(next))) {
                    throw e;
                }
                return;
            }
            case DELIVERED: {
                if (!DroneState.RETURNING.equals(next)) {
                    throw e;
                }
                return;
            }
            case RETURNING: {
                if (!DroneState.IDLE.equals(next)) {
                    throw e;
                }
            }
        }
    }
}
