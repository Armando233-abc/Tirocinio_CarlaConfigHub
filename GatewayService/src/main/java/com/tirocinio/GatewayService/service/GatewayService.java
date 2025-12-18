package com.tirocinio.GatewayService.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GatewayService {

    private final RestTemplate restTemplate;

    // Leggiamo gli URL dal file application.properties
    @Value("${service.weather.url}")
    private String weatherServiceUrl;

    @Value("${service.vehicle.url}")
    private String vehicleServiceUrl;

    @Value("${service.scenario.url}")
    private String scenarioServiceUrl;

    @Value("${service.composer.url}")
    private String composerServiceUrl;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String processConfiguration(Map<String, Object> requestData) {
        String weatherXml = callWeatherService(requestData.get("weatherParams"));
        String vehicleXml = callVehicleService(requestData.get("vehicleParams"));
        String scenarioXml = callScenarioService(requestData.get("scenarioParams"));


        Map<String, String> fragments = new HashMap<>();
        fragments.put("weatherFragment", weatherXml);
        fragments.put("vehicleFragment", vehicleXml);
        fragments.put("scenarioFragment", scenarioXml);


        return callComposerService(fragments);
    }


    // --- CHIAMATE AI MICROSERVIZI CON CIRCUIT BREAKER ---
    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackError")
    public String callWeatherService(Object params) {
        return restTemplate.postForObject(weatherServiceUrl + "/generate", params, String.class);
    }

    @CircuitBreaker(name = "vehicleService", fallbackMethod = "fallbackError")
    public String callVehicleService(Object params) {
        return restTemplate.postForObject(vehicleServiceUrl + "/generate", params, String.class);
    }

    @CircuitBreaker(name = "scenarioService", fallbackMethod = "fallbackError")
    public String callScenarioService(Object params) {
        return restTemplate.postForObject(scenarioServiceUrl + "/generate", params, String.class);
    }

    @CircuitBreaker(name = "composerService", fallbackMethod = "fallbackError")
    public String callComposerService(Map<String, String> fragments) {
        return restTemplate.postForObject(composerServiceUrl + "/compose", fragments, String.class);
    }

    public String fallbackError(Object params, Throwable t) {
        System.err.println("CIRCUIT BREAKER ATTIVATO: " + t.getMessage());
        // Restituiamo un XML di errore o null per essere gestito dal controller
        throw new RuntimeException("Servizio momentaneamente non disponibile: " + t.getMessage());
    }
}