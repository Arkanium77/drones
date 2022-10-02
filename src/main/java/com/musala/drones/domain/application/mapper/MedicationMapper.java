package com.musala.drones.domain.application.mapper;

import com.musala.drones.domain.application.dto.general.MedicationDto;
import com.musala.drones.domain.application.entity.MedicationEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class MedicationMapper {
    public abstract MedicationEntity toEntity(MedicationDto request);

    public abstract MedicationDto toDto(MedicationEntity droneEntity);
}
