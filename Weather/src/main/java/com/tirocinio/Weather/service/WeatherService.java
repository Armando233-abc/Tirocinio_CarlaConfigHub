package com.tirocinio.Weather.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class WeatherService {

    public String generateWeatherFragment(Map<String, Object> params) {

        // 1. Estrazione e Parsing (gestiamo il caso in cui i valori arrivino come String o Number)
        Double cloudiness = parseValue(params.get("cloudiness"));
        Double precipitation = parseValue(params.get("precipitation"));
        Double windIntensity = parseValue(params.get("windIntensity"));
        Double sunAltitude = parseValue(params.get("sunAltitudeAngle"));

        // 2. Validazione (Input non validi -> Errore dettagliato) [cite: 28, 29]
        validatePercentage(cloudiness, "cloudiness");
        validatePercentage(precipitation, "precipitation");
        validatePercentage(windIntensity, "windIntensity");

        // 3. Generazione XML [cite: 26]
        StringBuilder xml = new StringBuilder();
        xml.append("<Weather>\n");
        xml.append("    <Cloudiness value=\"").append(cloudiness).append("\"/>\n");
        xml.append("    <Precipitation value=\"").append(precipitation).append("\"/>\n");
        xml.append("    <WindIntensity value=\"").append(windIntensity).append("\"/>\n");
        // L'angolo del sole può andare da -90 a 90 o 0-180, non lo validiamo come percentuale
        xml.append("    <SunAltitudeAngle value=\"").append(sunAltitude).append("\"/>\n");
        xml.append("</Weather>");



        return xml.toString();
    }

    // --- Metodi Helper ---

    private Double parseValue(Object value) {
        if (value == null) return 0.0; // Valore di default
        return Double.valueOf(value.toString());
    }

    private void validatePercentage(Double value, String paramName) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Il parametro '" + paramName + "' deve essere compreso tra 0 e 100. Valore ricevuto: " + value);
        }
    }
}