package com.musala.drones.domain.application.repository;

import com.musala.drones.domain.application.entity.DroneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<DroneEntity, String> {
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM public.drone d WHERE d.battery_capacity >= 25 AND d.weight_limit > (SELECT COALESCE(SUM(m.weight), 0.0) FROM public.load l JOIN medication m ON m.code = l.medication WHERE l.drone = d.serial_number);"
    )
    List<DroneEntity> findAvailableForLoading();
}
