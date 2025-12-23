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

        // 1. Parsing input
        String model = (String) params.get("model");
        String color = (String) params.get("color");
        Boolean autopilot = parseBoolean(params.get("autopilot"));

        // 2. Validazione
        validateModel(model);
        validateColor(color);

        String rgbColor = convertToRgb(color);

        // 3. Generazione XML OpenSCENARIO
        StringBuilder xml = new StringBuilder();

        xml.append("<ScenarioObject name=\"EgoVehicle\">\n");
        xml.append("  <Vehicle name=\"")
                .append(model)
                .append("\" vehicleCategory=\"car\">\n");

        // Performance (obbligatorio)
        xml.append("    <Performance maxSpeed=\"50\" maxAcceleration=\"3.0\" maxDeceleration=\"6.0\"/>\n");

        // BoundingBox (obbligatorio)
        xml.append("    <BoundingBox>\n");
        xml.append("      <Center x=\"1.5\" y=\"0.0\" z=\"0.9\"/>\n");
        xml.append("      <Dimensions width=\"2.0\" length=\"4.5\" height=\"1.5\"/>\n");
        xml.append("    </BoundingBox>\n");

        // Proprietà CARLA (colore)
        xml.append("    <Properties>\n");
        xml.append("      <Property name=\"color\" value=\"")
                .append(rgbColor)
                .append("\"/>\n");
        xml.append("    </Properties>\n");

        xml.append("  </Vehicle>\n");
        xml.append("</ScenarioObject>");

        // 4. Se autopilot attivo → aggiungiamo ControllerAction
        if (autopilot) {
            xml.append("\n");
            xml.append(generateAutopilotFragment());
        }

        return xml.toString();
    }

    // ---------------- Helper ----------------

    private void validateModel(String model) {
        if (model == null || !SUPPORTED_MODELS.contains(model)) {
            throw new IllegalArgumentException(
                    "Modello veicolo non supportato o nullo: " + model
            );
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

    /**
     * Conversione HEX → RGB (formato CARLA)
     */
    private String convertToRgb(String colorInput) {
        if (colorInput.startsWith("#")) {
            int r = Integer.parseInt(colorInput.substring(1, 3), 16);
            int g = Integer.parseInt(colorInput.substring(3, 5), 16);
            int b = Integer.parseInt(colorInput.substring(5, 7), 16);
            return r + "," + g + "," + b;
        }
        return colorInput;
    }

    /**
     * Controller CARLA Autopilot (OpenSCENARIO)
     */
    private String generateAutopilotFragment() {
        return """
        <Private entityRef="EgoVehicle">
          <PrivateAction>
            <ControllerAction>
              <AssignControllerAction>
                <Controller name="carla_autopilot"/>
              </AssignControllerAction>
            </ControllerAction>
          </PrivateAction>
        </Private>
        """;
    }
}
