package com.tirocinio.GatewayService.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GatewayService {
    private final RestTemplate restTemplate;
    private int counter = 0;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String orchestrateSimulation() {
        counter += 1;
        return "counter: " + counter;
    }
}
