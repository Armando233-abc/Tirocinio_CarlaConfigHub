package com.tirocinio.Weather.controller;

import com.tirocinio.Weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(
            summary = "Genera frammento XML Meteo",
            description = "Riceve i parametri meteorologici dal Config Service, li valida e produce il frammento XML conforme per CARLA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frammento XML generato con successo",
                    content = @Content(mediaType = "application/xml", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Input non valido: parametri fuori scala o errati. Nessun XML prodotto."),
            @ApiResponse(responseCode = "500", description = "Errore interno del server durante la generazione.")
            })
    @PostMapping("/generate")
    public ResponseEntity<?> generateWeather(@RequestBody Map<String, Object> request) {
        try {
            // Log per debug
            System.out.println("WeatherService: Ricevuta richiesta sulla porta 8082: " + request);

            String xml = weatherService.generateWeatherFragment(request);

            return ResponseEntity.ok(xml);

        } catch (IllegalArgumentException e) {
            // ERRORE DI INPUT [cite: 29]
            // Restituisce 400 Bad Request con il messaggio specifico (es. "cloudiness fuori range")
            return ResponseEntity.badRequest().body("Errore Validazione Weather: " + e.getMessage());

        } catch (Exception e) {
            // ERRORE INTERNO [cite: 31]
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore Interno Weather Service: Impossibile generare XML.");
        }
    }
}