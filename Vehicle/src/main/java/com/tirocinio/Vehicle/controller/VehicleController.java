package com.tirocinio.Vehicle.controller;

import com.tirocinio.Vehicle.service.VehicleService;
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
