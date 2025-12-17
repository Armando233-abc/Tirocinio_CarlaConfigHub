package com.tirocinio.GatewayService.controller;

import com.tirocinio.GatewayService.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping("/dispatch")
    public ResponseEntity<String> riceviConfigurazione() {
        String risultato = gatewayService.orchestrateSimulation();
        return ResponseEntity.ok(risultato);
    }
}