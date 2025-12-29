package com.tirocinio.Weather.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class WeatherService {

    public String generateWeatherFragment(Map<String, Object> params) {

        // 1. Parsing input
        Double cloudiness = parseValue(params.get("cloudiness"));          // 0–100
        Double precipitation = parseValue(params.get("precipitation"));    // 0–100
        Double windIntensity = parseValue(params.get("windIntensity"));    // 0–100
        Double sunAltitude = parseValue(params.get("sunAltitudeAngle"));   // -90–90

        // 2. Validazione
        validatePercentage(cloudiness, "cloudiness");
        validatePercentage(precipitation, "precipitation");
        validatePercentage(windIntensity, "windIntensity");
        validateSunAngle(sunAltitude);

        // 3. Conversioni per OpenSCENARIO
        String cloudState = mapCloudState(cloudiness);
        double rainIntensity = precipitation / 100.0;
        double windSpeed = windIntensity / 100.0;

        // 4. Generazione XML (solo blocco Weather)
        StringBuilder xml = new StringBuilder();

        xml.append("<Weather>\n");

        xml.append("  <Sun azimuth=\"0\" elevation=\"")
                .append(sunAltitude)
                .append("\"/>\n");

        xml.append("  <Precipitation precipitationType=\"rain\" intensity=\"")
                .append(rainIntensity)
                .append("\"/>\n");

        xml.append("  <CloudState cloudState=\"")
                .append(cloudState)
                .append("\"/>\n");

        xml.append("  <Wind direction=\"0\" speed=\"")
                .append(windSpeed)
                .append("\"/>\n");

        xml.append("</Weather>");

        return xml.toString();
    }

    // ---------- Helper ----------

    private Double parseValue(Object value) {
        if (value == null) return 0.0;
        return Double.valueOf(value.toString());
    }

    private void validatePercentage(Double value, String paramName) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(
                    "Il parametro '" + paramName + "' deve essere compreso tra 0 e 100. Valore ricevuto: " + value
            );
        }
    }

    private void validateSunAngle(Double value) {
        if (value < -90.0 || value > 90.0) {
            throw new IllegalArgumentException(
                    "SunAltitudeAngle deve essere compreso tra -90 e 90. Valore ricevuto: " + value
            );
        }
    }


    private String mapCloudState(Double cloudiness) {
        if (cloudiness < 20) return "skyOff";
        if (cloudiness < 50) return "fewClouds";
        if (cloudiness < 80) return "overcast";
        return "skyObscured";
    }
}
