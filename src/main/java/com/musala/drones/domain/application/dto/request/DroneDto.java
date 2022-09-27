package com.musala.drones.domain.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@With
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DroneDto {

    /*
    The serial number is limited only by length, but I believe that a number consisting of spaces is not valid,
    so I propose this restriction. If necessary, you can change it by correcting the regular expression
     */
    @NotBlank
    @Pattern(
            regexp = "^\\w{1,100}$",
            message = "Must contain only letters, numbers and the underscore character and be no more than one hundred characters long "
    )
    private final String serialNumber;
    @NotNull
    private final DroneModel model;
    @Max(value = 500)
    @Positive
    @NotNull
    private final Double weightLimit;
    @Positive
    @Min(value = 0)
    @Min(value = 100)
    @NotNull
    private final Integer batteryCapacity;
    @NotNull
    private final DroneState state;
}
