package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.enums.DroneState;
import com.musala.drones.domain.application.mapper.DroneMapper;
import com.musala.drones.domain.application.repository.DroneRepository;
import com.musala.drones.domain.ues.model.exception.ResourceNotFoundDroneAppException;
import com.musala.drones.domain.ues.model.exception.ValidationDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneService {
    private final DroneMapper mapper;
    private final LoadService loadService;
    private final DroneRepository droneRepository;

    /**
     * Registration of new drone
     *
     * @param request contains all required information for register of new drone
     * @return response with success status or throws exception
     */
    @Transactional
    public DroneInfoResponse register(DroneDto request) {
        DroneEntity entity = mapper.toEntity(request);
        droneRepository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public DroneEntity save(DroneDto request) {
        DroneEntity entity = mapper.toEntity(request);
        return droneRepository.save(entity);
    }

    @Transactional
    public void delete(DroneEntity entity) {
        droneRepository.delete(entity);
    }

    @Transactional
    public void deleteById(String id) {
        droneRepository.deleteById(id);
    }

    /**
     * Fetch drone information by id
     *
     * @param id drone identifier
     * @return all existing information about drone
     * @throws ResourceNotFoundDroneAppException if id is not registered in system
     */
    public DroneInfoResponse fetch(String id) {
        return droneRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundDroneAppException("Drone not registered"));
    }

    public DroneEntity findById(String id) {
        return droneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundDroneAppException("Drone not registered"));
    }

    @Transactional
    public DroneEntity changeState(DroneEntity drone, DroneState state) {
        checkChanging(drone.getState(), state);
        drone.setState(state);
        return droneRepository.save(drone);
    }

    public static void checkChanging(DroneState current, DroneState next) {
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
    @Transactional
    public DroneEntity clearLoad(DroneEntity drone) {
        loadService.deleteLoadByDrone(drone);
        drone.setLoad(new ArrayList<>());
        return drone;
    }
}
