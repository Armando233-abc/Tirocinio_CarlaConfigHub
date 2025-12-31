package com.tirocinio.GatewayService.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GatewayService gatewayService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gatewayService, "weatherServiceUrl", "http://weather-service");
        ReflectionTestUtils.setField(gatewayService, "vehicleServiceUrl", "http://vehicle-service");
        ReflectionTestUtils.setField(gatewayService, "scenarioServiceUrl", "http://scenario-service");
        ReflectionTestUtils.setField(gatewayService, "composerServiceUrl", "http://composer-service");
    }

    @Test
    @DisplayName("Orchestrazione Completa - Successo")
    void testProcessConfiguration_Success() {
        Map<String, Object> requestData = new HashMap<>();
        Map<String, Object> vehicleParams = new HashMap<>();
        vehicleParams.put("model", "vehicle.tesla.model3");
        vehicleParams.put("color", "Red");
        requestData.put("vehicleParams", vehicleParams);
        requestData.put("weatherParams", new HashMap<>());
        requestData.put("scenarioParams", new HashMap<>());

        when(restTemplate.postForObject(eq("http://weather-service/generate"), any(), eq(String.class)))
                .thenReturn("<Weather>OK</Weather>");
        when(restTemplate.postForObject(eq("http://vehicle-service/generate"), any(), eq(String.class)))
                .thenReturn("<Vehicle>OK</Vehicle>");
        when(restTemplate.postForObject(eq("http://scenario-service/generate"), any(), eq(String.class)))
                .thenReturn("<Scenario>OK</Scenario>");

        when(restTemplate.postForObject(eq("http://composer-service/compose"), anyMap(), eq(String.class)))
                .thenReturn("<FinalConfig>COMPLETE</FinalConfig>");

        String result = gatewayService.processConfiguration(requestData);

        assertEquals("<FinalConfig>COMPLETE</FinalConfig>", result);

        verify(restTemplate, times(1)).postForObject(eq("http://weather-service/generate"), any(), eq(String.class));
        verify(restTemplate, times(1)).postForObject(eq("http://vehicle-service/generate"), any(), eq(String.class));
        verify(restTemplate, times(1)).postForObject(eq("http://scenario-service/generate"), any(), eq(String.class));
        verify(restTemplate, times(1)).postForObject(eq("http://composer-service/compose"), anyMap(), eq(String.class));
    }

    @Test
    @DisplayName("Validazione Fallita - VehicleParams mancanti")
    void testProcessConfiguration_MissingVehicleParams() {
        Map<String, Object> requestData = new HashMap<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gatewayService.processConfiguration(requestData);
        });

        assertEquals("vehicleParams è mancante.", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Validazione Fallita - Modello Veicolo mancante")
    void testProcessConfiguration_InvalidVehicleParams() {
        Map<String, Object> requestData = new HashMap<>();
        Map<String, Object> vehicleParams = new HashMap<>();
        vehicleParams.put("color", "Red");
        requestData.put("vehicleParams", vehicleParams);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gatewayService.processConfiguration(requestData);
        });

        assertEquals("Parametro 'model' mancante o non valido.", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }
}