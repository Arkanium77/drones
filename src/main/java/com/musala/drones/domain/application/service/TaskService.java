package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final DroneRepository droneRepository;

    // See solution notes
    @Scheduled(cron = "${musala.auto-sync-cron}", zone = "Europe/Moscow")
    public void automaticBatteryStateLogging() {
        log.info("AutomaticBatteryStateLogging:: start");
        droneRepository.findAll().stream()
                .sorted(Comparator.comparing(DroneEntity::getSerialNumber))
                .forEachOrdered(de -> log.info("Drone [{}]. Battery capacity: {}%", de.getModel(), de.getBatteryCapacity()));
    }
}
