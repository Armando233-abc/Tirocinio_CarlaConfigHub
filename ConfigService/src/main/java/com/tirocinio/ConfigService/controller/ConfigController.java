package com.tirocinio.ConfigService.controller;


import com.tirocinio.ConfigService.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Home")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public String mostraDashboard(Model model) {
        String stato = configService.getStatoSistema();
        model.addAttribute("statoHtml", stato);
        return "Home";
    }

    @Operation(
            summary = "Avvia Creazione Simulazione",
            description = "Endpoint principale per l'utente. Riceve i parametri, li invia al Gateway e restituisce il file finale pronto per CARLA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File XML di configurazione generato con successo"),
            @ApiResponse(responseCode = "502", description = "Errore dal Gateway."),
            @ApiResponse(responseCode = "400", description = "Input utente non valido")
    })
    @PostMapping("/genera")
    @ResponseBody
    public String avviaGenerazione(Model model) {

        String risposta = configService.inviaDatiAlGateway();

        return risposta;
    }
}