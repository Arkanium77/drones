package com.musala.drones.controller;

import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/drone")
@Tag(name = "Dispatch controller for drone ruling")
public class DispatchController {
    private final DispatchService dispatchService;

    @PostMapping("")
    @Operation(
            summary = "Register a new drone",
            description = "A method of registering a new drone in the system"
    )
    @ApiResponse(
            responseCode = "201",
            description = "All available info about current drone state"
    )
    @ResponseStatus(value = HttpStatus.CREATED)
    public DroneInfoResponse register(@RequestBody @Valid DroneDto request) {
        return dispatchService.register(request);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Fetch drone info",
            description = "A method for fetching drone information by id"
    )
    @ApiResponse(responseCode = "200", description = "All available info about current drone state")
    @ApiResponse(responseCode = "404", description = "When drone not found in system by id")
    public DroneInfoResponse fetch(
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^\\w{1,100}$",
                    message = "Id must contain only letters, numbers and the underscore character and be " +
                            "no more than one hundred characters long "
            )
            String id
    ) {
        return dispatchService.fetch(id);
    }

    @PostMapping("/{id}/load")
    @Operation(
            summary = "Load the drone with cargo (full load description)",
            description = "A method for loading the drone with cargo. All previous cargo will be deleted."
    )
    @ApiResponse(responseCode = "200", description = "All available info about current drone state")
    @ApiResponse(responseCode = "404", description = "When drone not found in system by id")
    public DroneInfoResponse postLoad(
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^\\w{1,100}$",
                    message = "Id must contain only letters, numbers and the underscore character and be " +
                            "no more than one hundred characters long "
            )
            String id,
            @RequestBody @Valid List<DroneLoadPosition> loadPositions
    ) {
        return dispatchService.postLoad(id, loadPositions);
    }

    @PatchMapping("/{id}/load")
    @Operation(
            summary = "Load the drone with cargo (add new load)",
            description = "A method for loading the drone with cargo. New cargo will be added to exists"
    )
    @ApiResponse(responseCode = "200", description = "All available info about current drone state")
    @ApiResponse(responseCode = "404", description = "When drone not found in system by id")
    public DroneInfoResponse patchLoad(
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^\\w{1,100}$",
                    message = "Id must contain only letters, numbers and the underscore character and be " +
                            "no more than one hundred characters long "
            )
            String id,
            @RequestBody @Valid List<DroneLoadPosition> loadPositions
    ) {
        return dispatchService.patchLoad(id, loadPositions);
    }
}
