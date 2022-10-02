package com.musala.drones.domain.application.service;

import com.musala.drones.domain.application.dto.general.MedicationDto;
import com.musala.drones.domain.application.entity.MedicationEntity;
import com.musala.drones.domain.application.mapper.MedicationMapper;
import com.musala.drones.domain.application.repository.MedicationRepository;
import com.musala.drones.domain.ues.model.exception.ResourceNotFoundDroneAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationMapper mapper;
    private final MedicationRepository medicationRepository;

    /**
     * Find all medications
     *
     * @return all registered in system Medications
     */
    public Set<MedicationDto> findAll() {
        return medicationRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toSet());
    }

    public MedicationEntity findByCode(String code) {
        return medicationRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundDroneAppException("code"));
    }
    @Transactional
    public MedicationEntity save(MedicationEntity entity) {
        return medicationRepository.save(entity);
    }
    @Transactional
    public void delete(String medicationCode) {
        medicationRepository.deleteById(medicationCode);
    }
}
