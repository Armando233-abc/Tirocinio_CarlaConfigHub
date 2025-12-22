package com.tirocinio.Scenario.controller;
import com.tirocinio.Scenario.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Definizione Scenario di Simulazione",
            description = "Configura l'ambiente generale della simulazione, selezionando la Mappa e impostando il tempo di simulazione."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurazione scenario XML generata"),
            @ApiResponse(responseCode = "400", description = "Parametri scenario o mappa non validi. Nessun XML generato."),
            @ApiResponse(responseCode = "500", description = "Errore interno standardizzato notificato al Gateway Service.")
    })
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
