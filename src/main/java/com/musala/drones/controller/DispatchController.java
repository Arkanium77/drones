package com.musala.drones.controller;

import com.musala.drones.domain.application.dto.request.DroneDto;
import com.musala.drones.domain.application.dto.response.Response;
import com.musala.drones.domain.application.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/drone")
@Tag(name = "Dispatch controller for drone ruling")
public class DispatchController {
    private final DroneService droneService;

    @PostMapping("")
    @Operation(
            summary = "Register a new drone",
            description = "A method of registering a new drone in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Small description of operation result"
    )
    public Response register(@RequestBody @Valid DroneDto request) {
        return droneService.register(request);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Fetch drone info",
            description = "A method for fetching drone information by id"
    )
    @ApiResponse(responseCode = "200", description = "All available info about current drone state")
    @ApiResponse(responseCode = "404", description = "When drone not found in system by id")
    public DroneDto compare(
            @PathVariable
            @NotBlank
            @Pattern(
                    regexp = "^\\w{1,100}$",
                    message = "Id must contain only letters, numbers and the underscore character and be " +
                            "no more than one hundred characters long "
            )
            String id
    ) {
        return droneService.fetch(id);
    }
}
