package com.musala.drones.domain.application.repository;

import com.musala.drones.domain.application.entity.DroneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends JpaRepository<DroneEntity, String> {
}
