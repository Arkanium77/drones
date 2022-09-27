package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.dto.request.DroneDto;
import com.musala.drones.domain.application.dto.response.Response;
import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.mapper.DroneMapper;
import com.musala.drones.domain.application.repository.DroneRepository;
import com.musala.drones.domain.ues.model.exception.ResourceNotFoundDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneService {
    private final DroneMapper mapper;
    private final DroneRepository droneRepository;

    /**
     * Registration of new drone
     *
     * @param request contains all required information for register of new drone
     * @return response with success status or throws exception
     */
    public Response register(DroneDto request) {
        DroneEntity entity = mapper.toEntity(request);
        droneRepository.save(entity);
        return Response.ok("Drone " + entity.getSerialNumber() + " successful registered");
    }

    /**
     * Fetch drone information by id
     *
     * @param id drone identifier
     * @return all existing information about drone
     * @throws ResourceNotFoundDroneAppException if id is not registered in system
     */
    public DroneDto fetch(String id) {
        return droneRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundDroneAppException("Drone not registered"));
    }
}
