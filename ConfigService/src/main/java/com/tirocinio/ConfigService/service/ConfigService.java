package com.tirocinio.ConfigService.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConfigService {
    private final RestTemplate restTemplate;

    public ConfigService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public String getStatoSistema() {
        return "ATTIVO";
    }

    public String inviaDatiAlGateway() {
        String urlGateway = "http://localhost:8080/api/gateway";

        try {
            // Nota: passiamo 'null' o una stringa vuota perché al contatore non serve input
            String risposta = restTemplate.postForObject(urlGateway, "", String.class);
            return risposta;
        } catch (Exception e) {
            e.printStackTrace(); // Stampa l'errore in console per capire cosa succede
            return "Errore di connessione: " + e.getMessage();
        }
    }
}