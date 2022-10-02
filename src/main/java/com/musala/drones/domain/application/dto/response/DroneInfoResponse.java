package com.musala.drones.domain.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DroneInfoResponse extends DroneDto {

    @NotNull
    @Positive
    @Max(value = 500)
    private Double currentWeight;
    private Set<DroneLoadPosition> load;
}
