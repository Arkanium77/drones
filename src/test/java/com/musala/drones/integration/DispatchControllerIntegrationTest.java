package com.musala.drones.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.MedicationEntity;
import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import com.musala.drones.domain.application.service.DroneService;
import com.musala.drones.domain.application.service.LoadService;
import com.musala.drones.domain.application.service.MedicationService;
import com.musala.drones.domain.application.utils.NanoId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DispatchControllerIntegrationTest { // See solution notes
    private final static String ID = NanoId.next();
    private final static String MEDICATION_CODE = NanoId.next();
    private final static String ANOTHER_MEDICATION_CODE = NanoId.next();
    private final static String BASE_URL = "/v1/drone";
    private final static String SPECIFIC_DRONE_URL = "/%s";
    private final static String LOAD_SPECIFIC_DRONE_URL = "/load";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DroneService droneService;
    @Autowired
    private LoadService loadService;
    @Autowired
    private MedicationService medicationService;

    @BeforeEach
    void init() {
        DroneDto droneDto = DroneDto.builder()
                .serialNumber(ID)
                .batteryCapacity(100)
                .model(DroneModel.LIGHTWEIGHT)
                .state(DroneState.IDLE)
                .weightLimit(500.0)
                .build();
        droneService.register(droneDto);

        MedicationEntity medication = MedicationEntity.builder()
                .code(MEDICATION_CODE)
                .name("TEST MEDICATION")
                .weight(50.5)
                .build();
        MedicationEntity anotherMedication = MedicationEntity.builder()
                .code(ANOTHER_MEDICATION_CODE)
                .name("TEST MEDICATION")
                .weight(1.0)
                .build();
        medicationService.save(medication);
        medicationService.save(anotherMedication);
    }

    @AfterEach
    void teardown() {
        loadService.deleteLoadByDroneId(ID);
        medicationService.delete(MEDICATION_CODE);
        medicationService.delete(ANOTHER_MEDICATION_CODE);
        droneService.deleteById(ID);
    }

    @Test
    public void whenRequestCorrectCreateDroneReturn201() throws Exception {
        DroneDto droneDto = DroneDto.builder()
                .serialNumber(NanoId.next())
                .batteryCapacity(100)
                .model(DroneModel.LIGHTWEIGHT)
                .state(DroneState.IDLE)
                .weightLimit(500.0)
                .build();
        String data = objectMapper.writeValueAsString(droneDto);
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data)
        ).andExpect(status().isCreated());
        droneService.deleteById(droneDto.getSerialNumber());
    }

    @Test
    public void whenSuccessfullyCreateDroneFetchResultIsConsistent() throws Exception {
        String fetchRequestUrl = BASE_URL + SPECIFIC_DRONE_URL;
        MvcResult fetchResult = this.mockMvc.perform(get(String.format(fetchRequestUrl, ID)))
                .andExpect(status().isOk())
                .andReturn();
        DroneDto response = objectMapper.readValue(fetchResult.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(ID, response.getSerialNumber());
        assertEquals(100, response.getBatteryCapacity());
        assertEquals(DroneModel.LIGHTWEIGHT, response.getModel());
        assertEquals(DroneState.IDLE, response.getState());
        assertEquals(500.0, response.getWeightLimit());
    }

    @Test
    public void droneCanBeLoaded() throws Exception {
        String fetchRequestUrl = BASE_URL + SPECIFIC_DRONE_URL + LOAD_SPECIFIC_DRONE_URL;
        Integer medicationCount, anotherMedicationCount;
        List<DroneLoadPosition> droneLoadPositions;
        MvcResult result;
        DroneInfoResponse response;

        droneLoadPositions = IntStream.range(1, 3)
                .mapToObj(i -> DroneLoadPosition.builder()
                        .medication(MEDICATION_CODE)
                        .count(i)
                        .build()
                ).collect(Collectors.toList());
        IntStream.range(1, 3)
                .mapToObj(i -> DroneLoadPosition.builder()
                        .medication(ANOTHER_MEDICATION_CODE)
                        .count(i)
                        .build()
                ).forEach(droneLoadPositions::add);

        result = this.mockMvc.perform(patch(String.format(fetchRequestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(154.5, response.getCurrentWeight(), 0.1);
        assertEquals(2, response.getLoad().size());
        medicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        anotherMedicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(ANOTHER_MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        assertEquals(3, medicationCount);
        assertEquals(3, anotherMedicationCount);
        assertEquals(DroneState.LOADING, response.getState());


        droneLoadPositions.add(DroneLoadPosition.builder()
                .medication(ANOTHER_MEDICATION_CODE)
                .count(1)
                .build()
        );
        result = this.mockMvc.perform(post(String.format(fetchRequestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(155.5, response.getCurrentWeight(), 0.1);
        assertEquals(2, response.getLoad().size());
        medicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        anotherMedicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(ANOTHER_MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        assertEquals(3, medicationCount);
        assertEquals(4, anotherMedicationCount);
        assertEquals(DroneState.LOADING, response.getState());

        result = this.mockMvc.perform(patch(String.format(fetchRequestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(311, response.getCurrentWeight(), 0.1);
        assertEquals(2, response.getLoad().size());
        medicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        anotherMedicationCount = response.getLoad().stream()
                .filter(lp -> lp.getMedication().equals(ANOTHER_MEDICATION_CODE))
                .findAny().map(DroneLoadPosition::getCount).orElse(0);
        assertEquals(6, medicationCount);
        assertEquals(8, anotherMedicationCount);
        assertEquals(DroneState.LOADING, response.getState());

    }


}
