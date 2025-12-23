package com.tirocinio.Scenario.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ScenarioService {

    private static final List<String> VALID_TOWNS = Arrays.asList(
            "Town01", "Town02", "Town03", "Town04", "Town05", "Town10HD"
    );

    public String generateScenarioFragment(Map<String, Object> params) {

        // 1. Parsing input
        String town = (String) params.get("town");
        Integer pedestrians = parseInteger(params.get("pedestrians"));
        Double trafficDensity = parseDouble(params.get("trafficDensity"));

        // 2. Validazione
        validateTown(town);
        validatePositiveInteger(pedestrians, "pedestrians");
        validatePercentage(trafficDensity, "trafficDensity");

        // 3. Conversione densità → numero veicoli
        int vehicleCount = mapTrafficDensityToVehicles(trafficDensity);

        // 4. Generazione XML
        StringBuilder xml = new StringBuilder();

        // ---- Road Network ----
        xml.append("<RoadNetwork>\n");
        xml.append("  <LogicFile filepath=\"")
                .append(town)
                .append("\"/>\n");
        xml.append("</RoadNetwork>\n\n");

        // ---- Traffic + Pedestrians ----
        xml.append("<GlobalAction>\n");
        xml.append("  <UserDefinedAction>\n");
        xml.append("    <CustomCommandAction>\n");
        xml.append("      <Command><![CDATA[\n");
        xml.append("spawn_traffic_vehicles(")
                .append(vehicleCount)
                .append(");\n");
        xml.append("spawn_pedestrians(")
                .append(pedestrians)
                .append(");\n");
        xml.append("]]></Command>\n");
        xml.append("    </CustomCommandAction>\n");
        xml.append("  </UserDefinedAction>\n");
        xml.append("</GlobalAction>");

        return xml.toString();
    }

    // ---------------- Helper ----------------

    private void validateTown(String town) {
        if (town == null || !VALID_TOWNS.contains(town)) {
            throw new IllegalArgumentException("Mappa non valida o inesistente: " + town);
        }
    }

    private void validatePositiveInteger(Integer value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Il campo '" + fieldName + "' non può essere negativo."
            );
        }
    }

    private void validatePercentage(Double value, String fieldName) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(
                    "Il campo '" + fieldName + "' deve essere tra 0 e 100."
            );
        }
    }

    private Integer parseInteger(Object value) {
        if (value == null) return 0;
        return Integer.valueOf(value.toString());
    }

    private Double parseDouble(Object value) {
        if (value == null) return 0.0;
        return Double.valueOf(value.toString());
    }

    /**
     * Mapping densità (%) → numero veicoli
     */
    private int mapTrafficDensityToVehicles(Double density) {
        if (density < 10) return 5;
        if (density < 30) return 15;
        if (density < 60) return 30;
        if (density < 80) return 50;
        return 70;
    }
}

