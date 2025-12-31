package com.tirocinio.Scenario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirocinio.Scenario.service.ScenarioService;
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

@WebMvcTest(ScenarioController.class)
class ScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScenarioService scenarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /generate - 200 OK con XML valido")
    void testGenerateScenario_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("town", "Town01");
        request.put("trafficDensity", 50.0);

        String expectedXml = "<RoadNetwork>...</RoadNetwork>";

        when(scenarioService.generateScenarioFragment(anyMap())).thenReturn(expectedXml);

        mockMvc.perform(post("/api/scenario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedXml));
    }

    @Test
    @DisplayName("POST /generate - 400 Bad Request su Input non valido")
    void testGenerateScenario_BadRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("town", "GothamCity");

        when(scenarioService.generateScenarioFragment(anyMap()))
                .thenThrow(new IllegalArgumentException("Mappa non valida: GothamCity"));

        mockMvc.perform(post("/api/scenario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Errore Validazione Scenario: Mappa non valida: GothamCity"));
    }

    @Test
    @DisplayName("POST /generate - 500 Internal Server Error su errore generico")
    void testGenerateScenario_InternalError() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("town", "Town01");

        when(scenarioService.generateScenarioFragment(anyMap()))
                .thenThrow(new RuntimeException("Errore di connessione"));

        mockMvc.perform(post("/api/scenario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore Interno Scenario Service."));
    }
}