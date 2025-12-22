package com.tirocinio.Vehicle.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VehicleService {
    private static final List<String> SUPPORTED_MODELS = Arrays.asList(
            "vehicle.tesla.model3",
            "vehicle.audi.etron",
            "vehicle.nissan.patrol",
            "vehicle.mustang.mustang"
    );

    public String generateVehicleFragment(Map<String, Object> params) {

        String model = (String) params.get("model");
        String color = (String) params.get("color");
        Boolean autopilot = parseBoolean(params.get("autopilot"));


        validateModel(model);
        validateColor(color);


        StringBuilder xml = new StringBuilder();
        xml.append("<Vehicle>\n");
        xml.append("    <Model value=\"").append(model).append("\"/>\n");


        String rgbColor = convertToRgb(color);
        xml.append("    <Color value=\"").append(rgbColor).append("\"/>\n");

        xml.append("    <Autopilot value=\"").append(autopilot).append("\"/>\n");
        xml.append("</Vehicle>");

        return xml.toString();
    }


    private void validateModel(String model) {
        if (model == null || !SUPPORTED_MODELS.contains(model)) {
            throw new IllegalArgumentException("Modello veicolo non supportato o nullo: " + model);
        }
    }

    private void validateColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("Il colore è obbligatorio.");
        }
    }

    private Boolean parseBoolean(Object value) {
        if (value == null) return false;
        return Boolean.parseBoolean(value.toString());
    }


    private String convertToRgb(String colorInput) {
        if (colorInput.startsWith("#")) {
            int r = Integer.valueOf(colorInput.substring(1, 3), 16);
            int g = Integer.valueOf(colorInput.substring(3, 5), 16);
            int b = Integer.valueOf(colorInput.substring(5, 7), 16);
            return r + "," + g + "," + b;
        }
        return colorInput;
    }
}
