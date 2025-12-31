package com.tirocinio.Scenario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioServiceTest {

    private ScenarioService scenarioService;

    @BeforeEach
    void setUp() {
        scenarioService = new ScenarioService();
    }

    @Test
    @DisplayName("Generazione XML con successo - Mappa valida e Densità media")
    void testGenerateScenarioFragment_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "Town01");
        params.put("trafficDensity", 50.0);

        String resultXml = scenarioService.generateScenarioFragment(params);

        assertNotNull(resultXml);
        assertTrue(resultXml.contains("<RoadNetwork>"));
        assertTrue(resultXml.contains("filepath=\"Town01\""));

        assertTrue(resultXml.contains("name=\"Traffic0\""));
        assertTrue(resultXml.contains("name=\"Traffic4\""));
        assertFalse(resultXml.contains("name=\"Traffic5\""));
    }

    @Test
    @DisplayName("Logica Densità Traffico - Bassa Densità")
    void testGenerateScenarioFragment_LowDensity() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "Town02");
        params.put("trafficDensity", 10.0);

        String resultXml = scenarioService.generateScenarioFragment(params);

        assertTrue(resultXml.contains("name=\"Traffic0\""));
        assertTrue(resultXml.contains("name=\"Traffic1\""));
        assertFalse(resultXml.contains("name=\"Traffic2\""));
    }

    @Test
    @DisplayName("Logica Densità Traffico - Alta Densità")
    void testGenerateScenarioFragment_HighDensity() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "Town10HD");
        params.put("trafficDensity", 80.0);

        String resultXml = scenarioService.generateScenarioFragment(params);

        assertTrue(resultXml.contains("name=\"Traffic9\""));
    }

    @Test
    @DisplayName("Errore Validazione - Mappa non valida")
    void testGenerateScenarioFragment_InvalidTown() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "InvalidTown");
        params.put("trafficDensity", 50.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scenarioService.generateScenarioFragment(params);
        });

        assertEquals("Mappa non valida: InvalidTown", exception.getMessage());
    }

    @Test
    @DisplayName("Errore Validazione - Densità fuori range (>100)")
    void testGenerateScenarioFragment_InvalidDensityHigh() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "Town03");
        params.put("trafficDensity", 150.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scenarioService.generateScenarioFragment(params);
        });

        assertEquals("Il campo 'trafficDensity' deve essere tra 0 e 100.", exception.getMessage());
    }

    @Test
    @DisplayName("Errore Validazione - Densità fuori range (<0)")
    void testGenerateScenarioFragment_InvalidDensityLow() {
        Map<String, Object> params = new HashMap<>();
        params.put("town", "Town03");
        params.put("trafficDensity", -5.0);

        assertThrows(IllegalArgumentException.class, () -> {
            scenarioService.generateScenarioFragment(params);
        });
    }
}