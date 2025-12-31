package com.tirocinio.Vehicle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService();
    }

    @Test
    @DisplayName("Generazione XML con successo - Modello valido e colore semplice")
    void testGenerateVehicleFragment_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "vehicle.tesla.model3");
        params.put("color", "Red");

        String resultXml = vehicleService.generateVehicleFragment(params);

        assertNotNull(resultXml);
        assertTrue(resultXml.contains("<Vehicle name=\"vehicle.tesla.model3\""));
        assertTrue(resultXml.contains("value=\"Red\""));
        assertTrue(resultXml.contains("maxSpeed=\"50\""));
    }

    @Test
    @DisplayName("Conversione Colore HEX in RGB - Logica Helper")
    void testGenerateVehicleFragment_HexColorConversion() {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "vehicle.audi.etron");
        params.put("color", "#FF0000");


        String resultXml = vehicleService.generateVehicleFragment(params);

       assertTrue(resultXml.contains("value=\"255,0,0\""));
    }

    @Test
    @DisplayName("Errore Validazione - Modello non supportato")
    void testGenerateVehicleFragment_InvalidModel() {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "vehicle.fiat.panda");
        params.put("color", "Blue");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.generateVehicleFragment(params);
        });

        assertEquals("Modello veicolo non supportato: vehicle.fiat.panda", exception.getMessage());
    }

    @Test
    @DisplayName("Errore Validazione - Colore mancante")
    void testGenerateVehicleFragment_MissingColor() {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "vehicle.mustang.mustang");
        params.put("color", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.generateVehicleFragment(params);
        });

        assertEquals("Il colore è obbligatorio.", exception.getMessage());
    }
}