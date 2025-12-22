package com.tirocinio.Composer.controller;


import com.tirocinio.Composer.service.ComposerService;
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