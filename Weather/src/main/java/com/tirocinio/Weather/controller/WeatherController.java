package com.tirocinio.Weather.controller;

import com.tirocinio.Weather.service.WeatherService;
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