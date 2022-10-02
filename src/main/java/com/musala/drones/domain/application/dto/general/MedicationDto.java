package com.musala.drones.domain.application.dto.general;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Jacksonized
@Builder
public class MedicationDto {
    @NotBlank
    private final String code;
    @NotBlank
    private final String name;
    @Max(value = 500)
    @Positive
    @NotNull
    private final Double weight;
    private final String image;
}
