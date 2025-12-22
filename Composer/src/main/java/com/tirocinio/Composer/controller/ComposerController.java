package com.tirocinio.Composer.controller;


import com.tirocinio.Composer.service.ComposerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/composer")
public class ComposerController {

    private final ComposerService composerService;

    @Autowired
    public ComposerController(ComposerService composerService) {
        this.composerService = composerService;
    }

    @Operation(
            summary = "Assemblaggio e Validazione Finale",
            description = "Aggrega i frammenti XML ricevuti dal gateway in un unico documento e verifica la conformità sintattica e strutturale."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File di configurazione CARLA completo e validato"),
            @ApiResponse(responseCode = "422", description = "Errore di validazione: l'XML risultante è sintatticamente errato o incompleto."),
            @ApiResponse(responseCode = "500", description = "Errore durante il processo di assemblaggio")
    })
    @PostMapping("/compose")
    public ResponseEntity<?> compose(@RequestBody Map<String, String> fragments) {
        try {
            System.out.println("ComposerService (Port 8085): Ricevuti frammenti da assemblare.");

            String finalXml = composerService.composeConfiguration(fragments);

            return ResponseEntity.ok(finalXml);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore Composer: " + e.getMessage());
        }
    }
}