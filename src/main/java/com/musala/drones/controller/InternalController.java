package com.musala.drones.controller;

import com.musala.drones.domain.application.utils.NanoId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {
    @GetMapping("/id")
    public String getNextId() {
        return NanoId.next();
    }
}
