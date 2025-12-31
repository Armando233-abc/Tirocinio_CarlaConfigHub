package com.tirocinio.Vehicle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirocinio.Vehicle.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /generate - 200 OK con XML valido")
    void testGenerateVehicle_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "vehicle.tesla.model3");
        request.put("color", "White");

        String expectedXml = "<ScenarioObject>...</ScenarioObject>";

        when(vehicleService.generateVehicleFragment(anyMap())).thenReturn(expectedXml);

        mockMvc.perform(post("/api/vehicle/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedXml));
    }

    @Test
    @DisplayName("POST /generate - 400 Bad Request su Input non valido")
    void testGenerateVehicle_BadRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "invalid.model");

        when(vehicleService.generateVehicleFragment(anyMap()))
                .thenThrow(new IllegalArgumentException("Modello veicolo non supportato"));

        mockMvc.perform(post("/api/vehicle/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // Verifica codice 400
                .andExpect(content().string("Errore Validazione Veicolo: Modello veicolo non supportato"));
    }

    @Test
    @DisplayName("POST /generate - 500 Internal Server Error su errore generico")
    void testGenerateVehicle_InternalError() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "vehicle.tesla.model3");

        when(vehicleService.generateVehicleFragment(anyMap()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/vehicle/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore Interno Vehicle Service."));
    }
}