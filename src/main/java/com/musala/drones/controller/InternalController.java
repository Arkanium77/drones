package com.musala.drones.controller;

import com.musala.drones.domain.application.dto.general.MedicationDto;
import com.musala.drones.domain.application.service.MedicationService;
import com.musala.drones.domain.application.utils.NanoId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {
    private final MedicationService medicationService;

    @GetMapping("/medications")
    public Set<MedicationDto> getMedications() {
        return medicationService.findAll();
    }

    @GetMapping("/id")
    public String getNextId() {
        return NanoId.next();
    }
}
