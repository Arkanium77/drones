package com.musala.drones.domain.application.repository;

import com.musala.drones.domain.application.entity.LoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadRepository extends JpaRepository<LoadEntity, String> {
    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM public.load WHERE drone = :droneId")
    void deleteAllByDrone(String droneId);
}
