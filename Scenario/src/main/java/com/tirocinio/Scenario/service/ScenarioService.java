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


        String town = (String) params.get("town");
        Integer pedestrians = parseInteger(params.get("pedestrians"));
        Double trafficDensity = parseDouble(params.get("trafficDensity"));


        validateTown(town);
        validatePositiveInteger(pedestrians, "pedestrians");
        validatePercentage(trafficDensity, "trafficDensity");


        StringBuilder xml = new StringBuilder();
        xml.append("<Scenario>\n");
        xml.append("    <Town value=\"").append(town).append("\"/>\n");
        xml.append("    <Pedestrians value=\"").append(pedestrians).append("\"/>\n");
        xml.append("    <TrafficDensity value=\"").append(trafficDensity).append("\"/>\n");
        xml.append("</Scenario>");

        return xml.toString();
    }

    private void validateTown(String town) {
        if (town == null || !VALID_TOWNS.contains(town)) {
            throw new IllegalArgumentException("Mappa non valida o inesistente: " + town);
        }
    }

    private void validatePositiveInteger(Integer value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException("Il campo '" + fieldName + "' non può essere negativo.");
        }
    }

    private void validatePercentage(Double value, String fieldName) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Il campo '" + fieldName + "' deve essere tra 0 e 100.");
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
}