package com.musala.drones.domain.application.dto.general;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Jacksonized
@Builder
public class DroneLoadPosition {
    @NotBlank
    private final String medication;
    @Positive
    private final Integer count;
}
