package com.musala.drones.domain.application.mapper;

import com.musala.drones.domain.application.dto.request.DroneDto;
import com.musala.drones.domain.application.entity.DroneEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class DroneMapper {
    public abstract DroneEntity toEntity(DroneDto request);

    public abstract DroneDto toDto(DroneEntity droneEntity);
}
