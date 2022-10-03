package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.request.DronePatchRequest;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.enums.DroneState;
import com.musala.drones.domain.application.mapper.DroneMapper;
import com.musala.drones.domain.ues.model.exception.ResourceNotFoundDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {
    private final LoadService loadService;
    private final DroneMapper droneMapper;
    private final DroneService droneService;

    /**
     * Registration of new drone
     *
     * @param request contains all required information for register of new drone
     * @return response with success status or throws exception
     */
    @Transactional
    public DroneInfoResponse register(DroneDto request) {
        return droneService.register(request);
    }

    /**
     * Fetch drone information by id
     *
     * @param id drone identifier
     * @return all existing information about drone
     * @throws ResourceNotFoundDroneAppException if id is not registered in system
     */
    public DroneInfoResponse fetch(String id) {
        return droneService.fetch(id);
    }

    @Transactional
    public DroneInfoResponse postLoad(String id, List<DroneLoadPosition> loadPositions) {
        DroneEntity drone = droneService.findById(id);
        drone = droneService.changeState(drone, DroneState.LOADING);
        drone = droneService.clearLoad(drone);
        DroneInfoResponse response = loadService.load(drone, loadPositions);
        return droneMapper.update(response, drone);
    }

    @Transactional
    public DroneInfoResponse patchLoad(String id, List<DroneLoadPosition> loadPositions) {
        DroneEntity drone = droneService.findById(id);
        drone = droneService.changeState(drone, DroneState.LOADING);
        DroneInfoResponse response = loadService.load(drone, loadPositions);
        return droneMapper.update(response, drone);
    }

    @Transactional
    public DroneInfoResponse patchDrone(String id, DronePatchRequest request) {
        DroneEntity drone = droneService.patch(id, request);
        return droneMapper.toResponse(drone);
    }

    public List<DroneInfoResponse> findAvailableForLoading() {
        return droneService.findAvailableForLoading().stream()
                .map(droneMapper::toResponse)
                .collect(Collectors.toList());
    }
}
