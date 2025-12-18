package com.tirocinio.GatewayService.controller;

import com.tirocinio.GatewayService.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin(origins = "http://localhost:8081")
public class GatewayController {

    private final GatewayService gatewayService;

    @Autowired
    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateConfiguration(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("Gateway: Ricevuta richiesta di generazione config.");

            String finalXml = gatewayService.processConfiguration(requestData);

            return ResponseEntity.ok(finalXml);

        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body("Errore durante l'orchestrazione: " + e.getMessage());
        }
    }
}