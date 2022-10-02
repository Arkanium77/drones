package com.musala.drones.domain.application.repository;

import com.musala.drones.domain.application.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<MedicationEntity, String> {
}
