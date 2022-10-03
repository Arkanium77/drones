package com.musala.drones.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.domain.application.dto.general.DroneDto;
import com.musala.drones.domain.application.dto.general.DroneLoadPosition;
import com.musala.drones.domain.application.dto.request.DronePatchRequest;
import com.musala.drones.domain.application.dto.response.DroneInfoResponse;
import com.musala.drones.domain.application.entity.MedicationEntity;
import com.musala.drones.domain.application.enums.DroneModel;
import com.musala.drones.domain.application.enums.DroneState;
import com.musala.drones.domain.application.service.DroneService;
import com.musala.drones.domain.application.service.LoadService;
import com.musala.drones.domain.application.service.MedicationService;
import com.musala.drones.domain.application.utils.NanoId;
import com.musala.drones.domain.ues.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private final static String AVAILABLE_FOR_LOADING_URL = "/available_for_loading";

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
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        String requestUrl = BASE_URL + SPECIFIC_DRONE_URL + LOAD_SPECIFIC_DRONE_URL;
        MvcResult result;
        DroneInfoResponse response;
        List<DroneLoadPosition> droneLoadPositions;
        Integer medicationCount, anotherMedicationCount;

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

        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
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
        result = this.mockMvc.perform(post(String.format(requestUrl, ID))
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

        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
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

    @Test
    public void droneCantBeLoaded() throws Exception {
        String requestUrl = BASE_URL + SPECIFIC_DRONE_URL + LOAD_SPECIFIC_DRONE_URL;
        MvcResult result;
        String errorMessage;
        ErrorResponse errorResponse;
        List<DroneLoadPosition> droneLoadPositions;

        droneService.patch(ID, DronePatchRequest.builder()
                .batteryCapacity(10)
                .build());
        droneLoadPositions = IntStream.range(1, 3)
                .mapToObj(i -> DroneLoadPosition.builder()
                        .medication(MEDICATION_CODE)
                        .count(i)
                        .build()
                ).collect(Collectors.toList());

        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().is4xxClientError())
                .andReturn();
        errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), errorResponse.getHttpErrorCode());
        errorMessage = "The drone cannot be in the LOADING state when the charge level is less than 25%";
        assertEquals(errorMessage, errorResponse.getMessage());

        result = this.mockMvc.perform(post(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().is4xxClientError())
                .andReturn();

        errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), errorResponse.getHttpErrorCode());
        assertEquals(errorMessage, errorResponse.getMessage());

        droneService.patch(ID, DronePatchRequest.builder()
                .batteryCapacity(100)
                .build());
        this.mockMvc.perform(patch(String.format(requestUrl, ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(droneLoadPositions))
        ).andExpect(status().isOk());

        droneLoadPositions = droneLoadPositions.stream()
                .flatMap(lp -> IntStream.range(0, 100).mapToObj(i -> lp))
                .collect(Collectors.toList());
        result = this.mockMvc.perform(post(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(droneLoadPositions))
                ).andExpect(status().is4xxClientError())
                .andReturn();

        errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), errorResponse.getHttpErrorCode());
        assertTrue(errorResponse.getMessage().startsWith("Overload!"));

    }

    @Test
    public void droneCanBePatched() throws Exception {
        String requestUrl = BASE_URL + SPECIFIC_DRONE_URL;
        MvcResult result;
        DroneInfoResponse response;
        ErrorResponse errorResponse;
        DronePatchRequest patchRequest;

        patchRequest = DronePatchRequest.builder()
                .state(DroneState.LOADING)
                .build();
        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest))
                ).andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(ID, response.getSerialNumber());
        assertEquals(100, response.getBatteryCapacity());
        assertEquals(DroneModel.LIGHTWEIGHT, response.getModel());
        assertEquals(DroneState.LOADING, response.getState());
        assertEquals(500.0, response.getWeightLimit());

        patchRequest = DronePatchRequest.builder()
                .batteryCapacity(25)
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(250.0)
                .state(DroneState.RETURNING)
                .build();

        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest))
                ).andExpect(status().is4xxClientError())
                .andReturn();
        errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertEquals(String.valueOf(HttpStatus.BAD_REQUEST.value()), errorResponse.getHttpErrorCode());
        String errorMessage = "Invalid state change operation. " +
                "Current state is " + DroneState.LOADING + ", but next state is " + DroneState.RETURNING;
        assertEquals(errorMessage, errorResponse.getMessage());

        patchRequest = DronePatchRequest.builder()
                .batteryCapacity(25)
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(250.0)
                .state(DroneState.LOADED)
                .build();

        result = this.mockMvc.perform(patch(String.format(requestUrl, ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest))
                ).andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), DroneInfoResponse.class);

        assertEquals(ID, response.getSerialNumber());
        assertEquals(25, response.getBatteryCapacity());
        assertEquals(DroneModel.CRUISERWEIGHT, response.getModel());
        assertEquals(DroneState.LOADED, response.getState());
        assertEquals(250.0, response.getWeightLimit());
    }

    @Test
    public void availableForLoading() throws Exception {
        String insertInititalState = "INSERT INTO public.medication (code, name, weight) VALUES ('1', 'first aid', 10), ('2', 'second aid', 20), ('3', 'third aid', 30); INSERT INTO public.drone (serial_number, model, weight_limit, battery_capacity, state) VALUES ('1', 'LIGHTWEIGHT', '100', '100', 'IDLE'), ('2', 'LIGHTWEIGHT', '100', '50', 'IDLE'), ('3', 'LIGHTWEIGHT', '100', '10', 'IDLE'); INSERT INTO public.load (id, drone, medication) VALUES ('A1', '1', '1'), ('A2', '1', '2'), ('B1', '2', '3'), ('B2', '2', '2');";
        String insertAdditionalLoad = "INSERT INTO public.load (id, drone, medication) VALUES ('B3', '2', '3'), ('B4', '2', '2');";
        String clear = "DELETE FROM public.load WHERE id IN ('A1', 'A2', 'B1', 'B2', 'B3', 'B4'); DELETE FROM public.medication WHERE code IN ('1', '2', '3'); DELETE FROM public.drone WHERE serial_number IN ('1', '2', '3');";
        String requestUrl = BASE_URL + AVAILABLE_FOR_LOADING_URL;
        MvcResult result;
        List<String> ids;
        List<DroneInfoResponse> response;

        jdbcTemplate.execute(insertInititalState);
        result = this.mockMvc.perform(get(requestUrl))
                .andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(3, response.size());
        ids = response.stream().map(DroneDto::getSerialNumber).collect(Collectors.toList());
        assertTrue(ids.containsAll(Arrays.asList("1", "2", ID)));

        jdbcTemplate.execute(insertAdditionalLoad);
        result = this.mockMvc.perform(get(requestUrl))
                .andExpect(status().isOk())
                .andReturn();
        response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertEquals(2, response.size());
        ids = response.stream().map(DroneDto::getSerialNumber).collect(Collectors.toList());
        assertTrue(ids.containsAll(Arrays.asList("1", ID)));

        jdbcTemplate.execute(clear);
    }


}
