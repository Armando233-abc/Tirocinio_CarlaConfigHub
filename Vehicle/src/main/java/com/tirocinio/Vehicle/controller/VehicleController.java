package com.tirocinio.Vehicle.controller;

import com.tirocinio.Vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(
            summary = "Configura Veicolo Ego",
            description = "Riceve il modello del veicolo e i parametri di dinamica di guida. Genera le specifiche tecniche XML per l'istanziazione nel simulatore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specifiche veicolo XML generate con successo"),
            @ApiResponse(responseCode = "400", description = "Dati veicolo non validi o modello inesistente."),
            @ApiResponse(responseCode = "500", description = "Errore interno standardizzato notificato al Gateway Service.")
    })
    @PostMapping("/generate")
    public ResponseEntity<?> generateVehicle(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("VehicleService (Port 8083): Ricevuta richiesta: " + request);

            String xml = vehicleService.generateVehicleFragment(request);

            return ResponseEntity.ok(xml);

        } catch (IllegalArgumentException e) {
            System.err.println("Errore validazione Veicolo: " + e.getMessage());
            return ResponseEntity.badRequest().body("Errore Validazione Veicolo: " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Errore Interno Vehicle Service.");
        }
    }
}
