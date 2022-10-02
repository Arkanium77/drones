package com.musala.drones.domain.application.enums;

import com.musala.drones.domain.ues.model.exception.ValidationDroneAppException;

public enum DroneState {
    IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING;
}
