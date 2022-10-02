package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.entity.LoadEntity;
import com.musala.drones.domain.application.entity.MedicationEntity;
import com.musala.drones.domain.application.mapper.DroneMapper;
import com.musala.drones.domain.application.repository.LoadRepository;
import com.musala.drones.domain.ues.model.exception.ValidationDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadService {
    private final DroneMapper mapper;
    private final LoadRepository loadRepository;
    private final MedicationService medicationService;

    /**
     * Reduce drone load positions from user
     *
     * @param request drone load positions from user
     * @return reduced list with only one block for each medication
     */
    public List<DroneLoadPosition> regroup(List<DroneLoadPosition> request) {
        return request.stream()
                .collect(Collectors.groupingBy(DroneLoadPosition::getMedication)).entrySet().stream()
                .map(e -> DroneLoadPosition.builder()
                        .medication(e.getKey())
                        .count(e.getValue().stream()
                                .mapToInt(DroneLoadPosition::getCount)
                                .sum()
                        )
                        .build()
                ).collect(Collectors.toList());

    }

    @Transactional
    public LoadEntity save(LoadEntity load) {
        return loadRepository.save(load);
    }

    @Transactional
    public void deleteLoadByDrone(DroneEntity drone) {
        loadRepository.deleteAllByDrone(drone.getSerialNumber());
    }

    @Transactional
    public void deleteLoadByDroneId(String id) {
        loadRepository.deleteAllByDrone(id);
    }

    @Transactional
    public DroneInfoResponse load(DroneEntity drone, List<DroneLoadPosition> loadPositions) {
        loadPositions = regroup(loadPositions);
        List<MedicationEntity> medications = loadPositions.stream()
                .flatMap(lp -> IntStream.range(0, lp.getCount())
                        .mapToObj(i -> medicationService.findByCode(lp.getMedication()))
                ).collect(Collectors.toList());
        double currentWeight = calculateAfterloadWeight(drone, medications);
        Set<LoadEntity> load = medications.stream()
                .map(me -> LoadEntity.builder()
                        .medication(me)
                        .drone(drone)
                        .build()
                )
                .map(loadRepository::save)
                .collect(Collectors.toSet());

        Optional.ofNullable(drone.getLoad())
                .ifPresent(load::addAll);

        return DroneInfoResponse.builder()
                .currentWeight(currentWeight)
                .load(mapper.calculateLoadPositions(load))
                .build();
    }

    private double calculateAfterloadWeight(DroneEntity drone, List<MedicationEntity> medications) {
        double currentWeight = mapper.calculateLoadWeight(drone);
        currentWeight += medications.stream()
                .mapToDouble(MedicationEntity::getWeight)
                .sum();
        if (currentWeight > drone.getWeightLimit()) {
            throw new ValidationDroneAppException("Overload! Loaded items weight is "
                    + currentWeight + "g., but drone has weight limit in "
                    + drone.getWeightLimit() + "g."
            );
        }
        return currentWeight;
    }
}
