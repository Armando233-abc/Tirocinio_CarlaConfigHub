package com.tirocinio.Scenario.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ScenarioService {

    private static final List<String> VALID_TOWNS = Arrays.asList(
            "Town01", "Town02", "Town03", "Town04", "Town05", "Town10HD"
    );

    private final Random random = new Random();

    public String generateScenarioFragment(Map<String, Object> params) {

        String town = (String) params.get("town");
        Double trafficDensity = parseDouble(params.get("trafficDensity"));

        validateTown(town);
        validatePercentage(trafficDensity, "trafficDensity");

        int vehicleCount = mapTrafficDensityToVehicles(trafficDensity);
        StringBuilder xml = new StringBuilder();

        // 1. ROAD NETWORK (Conforme: LogicFile è l'unico obbligatorio)
        xml.append("<RoadNetwork>\n");
        xml.append("  <LogicFile filepath=\"").append(town).append("\"/>\n");
        xml.append("</RoadNetwork>\n\n");

        // 2. TRAFFIC ENTITIES (Genera blocchi ScenarioObject per il traffico)
        // 2. TRAFFIC ENTITIES (Genera blocchi ScenarioObject per il traffico)
        for (int i = 0; i < vehicleCount; i++) {
            String name = "Traffic" + i;
            xml.append("<ScenarioObject name=\"").append(name).append("\">\n");
            xml.append("  <Vehicle name=\"vehicle.tesla.model3\" vehicleCategory=\"car\">\n");

            // Ordine XSD: BoundingBox -> Performance -> Axles -> Properties

            // 1. BoundingBox
            xml.append("    <BoundingBox>\n");
            xml.append("      <Center x=\"1.5\" y=\"0.0\" z=\"0.9\"/>\n");
            xml.append("      <Dimensions width=\"2.0\" length=\"4.5\" height=\"1.5\"/>\n");
            xml.append("    </BoundingBox>\n");

            // 2. Performance
            xml.append("    <Performance maxSpeed=\"50\" maxAcceleration=\"3.0\" maxDeceleration=\"6.0\"/>\n");

            // 3. Axles (UNA SOLA VOLTA)
            xml.append("    <Axles>\n");
            xml.append("      <FrontAxle maxSteering=\"0.5\" wheelDiameter=\"0.6\" trackWidth=\"1.8\" positionX=\"2.8\" positionZ=\"0.3\"/>\n");
            xml.append("      <RearAxle maxSteering=\"0.0\" wheelDiameter=\"0.6\" trackWidth=\"1.8\" positionX=\"0.0\" positionZ=\"0.3\"/>\n");
            xml.append("    </Axles>\n");

            // 4. Properties (UNA SOLA VOLTA)
            xml.append("    <Properties/>\n");

            xml.append("  </Vehicle>\n");
            xml.append("</ScenarioObject>\n\n");
        }

        // 3. PRIVATE ACTIONS (Init per il traffico)
        for (int i = 0; i < vehicleCount; i++) {
            String name = "Traffic" + i;
            xml.append("<Private entityRef=\"").append(name).append("\">\n");

            // Teleport Action con WorldPosition completa
            xml.append("  <PrivateAction>\n");
            xml.append("    <TeleportAction>\n");
            xml.append("      <Position>\n");
            xml.append("        <WorldPosition x=\"").append(format(getRandomCoordinate(100, 200)))
                    .append("\" y=\"").append(format(getRandomCoordinate(100, 200)))
                    .append("\" z=\"0.3\" h=\"0.0\" p=\"0.0\" r=\"0.0\"/>\n");
            xml.append("      </Position>\n");
            xml.append("    </TeleportAction>\n");
            xml.append("  </PrivateAction>\n");

            // Controller Action con Properties (richiesto per validazione XSD se presente Controller)
            xml.append("  <PrivateAction>\n");
            xml.append("    <ControllerAction>\n");
            xml.append("      <AssignControllerAction>\n");
            xml.append("        <Controller name=\"ExternalControl\">\n");
            xml.append("          <Properties/>\n");
            xml.append("        </Controller>\n");
            xml.append("      </AssignControllerAction>\n");
            xml.append("      <OverrideControllerValueAction>\n");
            xml.append("        <Throttle value=\"0.0\" active=\"false\"/>\n");
            xml.append("        <Brake value=\"0.0\" active=\"false\"/>\n");
            xml.append("        <Clutch value=\"0.0\" active=\"false\"/>\n");
            xml.append("        <ParkingBrake value=\"0.0\" active=\"false\"/>\n");
            xml.append("        <SteeringWheel value=\"0.0\" active=\"false\"/>\n");
            xml.append("        <Gear number=\"1\" active=\"false\"/>\n");
            xml.append("      </OverrideControllerValueAction>\n");
            xml.append("    </ControllerAction>\n");
            xml.append("  </PrivateAction>\n");

            xml.append("</Private>\n\n");
        }

        return xml.toString().trim();
    }

    // ---------------- Helper Methods ----------------

    private double getRandomCoordinate(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private String format(double value) {
        return String.format("%.2f", value).replace(",", ".");
    }

    private void validateTown(String town) {
        if (town == null || !VALID_TOWNS.contains(town)) {
            throw new IllegalArgumentException("Mappa non valida: " + town);
        }
    }

    private void validatePercentage(Double value, String fieldName) {
        if (value == null || value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Il campo '" + fieldName + "' deve essere tra 0 e 100.");
        }
    }

    private Double parseDouble(Object value) {
        try {
            return value == null ? 0.0 : Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int mapTrafficDensityToVehicles(Double density) {
        if (density <= 0) return 0;
        if (density < 30) return 2;
        if (density < 70) return 5;
        return 10;
    }
}