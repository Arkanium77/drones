package com.musala.drones.domain.application.mapper;

import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.request.DronePatchRequest;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.DroneEntity;
import com.musala.drones.domain.application.entity.LoadEntity;
import com.musala.drones.domain.application.entity.MedicationEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@RequiredArgsConstructor
public abstract class DroneMapper {

    public abstract DroneEntity toEntity(DroneDto request);

    public abstract DroneDto toDto(DroneEntity droneEntity);

    @Mappings({
            @Mapping(target = "currentWeight", expression = "java(calculateLoadWeight(droneEntity))"),
            @Mapping(target = "load", expression = "java(calculateLoadPositions(droneEntity))")
    })
    public abstract DroneInfoResponse toResponse(DroneEntity droneEntity);

    public Double calculateLoadWeight(DroneEntity droneEntity) {
        if (droneEntity.getLoad() == null) {
            return null;
        }
        return droneEntity.getLoad().stream()
                .map(LoadEntity::getMedication)
                .mapToDouble(MedicationEntity::getWeight)
                .sum();
    }

    public Set<DroneLoadPosition> calculateLoadPositions(DroneEntity droneEntity) {
        if (droneEntity == null) {
            return null;
        }
        return calculateLoadPositions(droneEntity.getLoad());
    }

    public Set<DroneLoadPosition> calculateLoadPositions(Collection<LoadEntity> load) {
        if (load == null) {
            return null;
        }
        return load.stream()
                .map(LoadEntity::getMedication)
                .collect(Collectors.groupingBy(MedicationEntity::getCode)).entrySet()
                .stream()
                .map(e -> DroneLoadPosition.builder()
                        .medication(e.getKey())
                        .count(e.getValue().size())
                        .build()
                )
                .collect(Collectors.toSet());
    }

    @Mapping(target = "response.load", ignore = true)
    @Mapping(target = "response.currentWeight", ignore = true)
    public abstract DroneInfoResponse update(@MappingTarget DroneInfoResponse response, DroneEntity drone);

    @Mapping(target = "response.load", ignore = true)
    public abstract DroneEntity update(@MappingTarget DroneEntity drone, DronePatchRequest request);
}
