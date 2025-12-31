package com.tirocinio.GatewayService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirocinio.GatewayService.service.GatewayService;
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

@WebMvcTest(GatewayController.class)
class GatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // O @MockitoBean
    private GatewayService gatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /generate - 200 OK - Orchestrazione riuscita")
    void testGenerateConfiguration_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("someParam", "someValue");

        String finalXml = "<OpenDRIVE>...</OpenDRIVE>";

        when(gatewayService.processConfiguration(anyMap())).thenReturn(finalXml);

        mockMvc.perform(post("/api/gateway/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(finalXml));
    }

    @Test
    @DisplayName("POST /generate - 503 Service Unavailable - Errore Circuit Breaker")
    void testGenerateConfiguration_ServiceFailure() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();

        when(gatewayService.processConfiguration(anyMap()))
                .thenThrow(new RuntimeException("Servizio momentaneamente non disponibile: Timeout WeatherService"));

        mockMvc.perform(post("/api/gateway/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable()) // Codice 503
                .andExpect(content().string("Errore durante l'orchestrazione: Servizio momentaneamente non disponibile: Timeout WeatherService"));
    }

    @Test
    @DisplayName("POST /generate - 503 Service Unavailable - Errore di Validazione Gateway")
    void testGenerateConfiguration_ValidationFailure() throws Exception {

        Map<String, Object> request = new HashMap<>();
        when(gatewayService.processConfiguration(anyMap()))
                .thenThrow(new IllegalArgumentException("vehicleParams è mancante."));

        mockMvc.perform(post("/api/gateway/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Errore durante l'orchestrazione: vehicleParams è mancante."));
    }
}