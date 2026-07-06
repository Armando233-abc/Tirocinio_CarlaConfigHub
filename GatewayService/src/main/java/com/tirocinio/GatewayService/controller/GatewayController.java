package com.tirocinio.GatewayService.controller;

import com.tirocinio.GatewayService.service.GatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Orchestrazione Richieste",
            description = "Riceve la richiesta dal Config Service e la smista ai microservizi di competenza (Weather, Vehicle, Scenario, Composer). " +
                    "Raccoglie le risposte XML dai microservizi (Weather, Vehicle, Scenario) e le manda al Composer, attende la risposta da quest'ultimo e la manda al microservizio Config."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orchestrazione completata con successo. Ritorna l'XML finale aggregato dal Composer Service."),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida: parametri obbligatori mancanti o malformati.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "503", description = "Servizio Non Disponibile (Circuit Breaker): uno dei microservizi a valle (Weather, Vehicle, Scenario, Composer) non risponde o è in timeout.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Errore interno del Gateway durante lo smistamento.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/generate")
    public ResponseEntity<?> generateConfiguration(@RequestBody Map<String, Object> requestData) {
        System.out.println("Gateway: Ricevuta richiesta di generazione config.");
        try {
            String finalXml = gatewayService.processConfiguration(requestData);
            return ResponseEntity.ok(finalXml);

        } catch (IllegalArgumentException e) {
            // Errore di validazione dei parametri (Fail Fast)
            return ResponseEntity.badRequest()
                    .body("Parametri non validi: " + e.getMessage());

        } catch (RuntimeException e) {
            // Errore proveniente dal fallback del Circuit Breaker
            // (servizio a valle non raggiungibile o in errore)
            return ResponseEntity.status(503)
                    .body("Servizio momentaneamente non disponibile: " + e.getMessage());

        } catch (Exception e) {
            // Qualsiasi altro errore imprevisto
            return ResponseEntity.status(500)
                    .body("Errore interno durante l'orchestrazione: " + e.getMessage());
        }
    }
}