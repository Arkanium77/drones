package com.musala.drones.domain.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DronePatchRequest {

    private DroneModel model;

    @Min(value = 0)
    @Max(value = 500)
    private Double weightLimit;

    @Min(value = 0)
    @Max(value = 100)
    private Integer batteryCapacity;

    private DroneState state;
}
