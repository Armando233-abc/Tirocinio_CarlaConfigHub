package com.tirocinio.Scenario.controller;
import com.tirocinio.Scenario.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scenario")
public class ScenarioController {

    private final ScenarioService scenarioService;

    @Autowired
    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateScenario(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("ScenarioService (Port 8084): Ricevuta richiesta: " + request);

            String xml = scenarioService.generateScenarioFragment(request);

            return ResponseEntity.ok(xml);

        } catch (IllegalArgumentException e) {
            System.err.println("Errore validazione Scenario: " + e.getMessage());
            return ResponseEntity.badRequest().body("Errore Validazione Scenario: " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore Interno Scenario Service.");
        }
    }
}
