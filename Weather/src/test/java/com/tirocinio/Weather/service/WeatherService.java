package com.tirocinio.Weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService();
    }

    // 1. TEST in cui tutto va bene
    @Test
    void testGenerateWeatherFragment_ValoriStandard() {

        Map<String, Object> params = new HashMap<>();
        params.put("cloudiness", 50.0);
        params.put("precipitation", 80.0);
        params.put("windIntensity", 10.0);
        params.put("sunAltitudeAngle", 45.0);


        String xml = weatherService.generateWeatherFragment(params);


        assertNotNull(xml);
        assertTrue(xml.contains("cloudState=\"cloudy\""), "Deve mappare 50.0 a 'cloudy'");
        assertTrue(xml.contains("intensity=\"0.8\""), "Deve convertire precipitazione in scala 0-1");
        assertTrue(xml.contains("elevation=\"45.0\""), "L'elevazione del sole deve corrispondere");
    }

    // 2. TEST LOGICA NUVOLE
    @Test
    void testMappaturaStatiNuvolosi() {
        // Caso: SkyOff (< 10)
        assertTrue(weatherService.generateWeatherFragment(creaMappa(5.0)).contains("cloudState=\"skyOff\""));

        // Caso: Free (< 30)
        assertTrue(weatherService.generateWeatherFragment(creaMappa(20.0)).contains("cloudState=\"free\""));

        // Caso: Overcast (< 85)
        assertTrue(weatherService.generateWeatherFragment(creaMappa(80.0)).contains("cloudState=\"overcast\""));

        // Caso: Rainy (>= 85)
        assertTrue(weatherService.generateWeatherFragment(creaMappa(90.0)).contains("cloudState=\"rainy\""));
    }

    // 3. TEST Eccezioni
    @Test
    void testValidazioneErrori() {
        // Caso: Cloudiness > 100
        Map<String, Object> paramsTroppoAlto = new HashMap<>();
        paramsTroppoAlto.put("cloudiness", 150.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            weatherService.generateWeatherFragment(paramsTroppoAlto);
        });

        assertTrue(exception.getMessage().contains("deve essere compreso tra 0 e 100"));
    }

    @Test
    void testValidazioneSole() {
        // Caso: SunAltitude < -90
        Map<String, Object> paramsSoleSbagliato = new HashMap<>();
        paramsSoleSbagliato.put("sunAltitudeAngle", -100.0);

        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.generateWeatherFragment(paramsSoleSbagliato);
        });
    }

    // 4. TEST ROBUSTEZZA
    @Test
    void testParsingRobustezza() {
        Map<String, Object> paramsStrani = new HashMap<>();
        paramsStrani.put("cloudiness", "50");
        paramsStrani.put("precipitation", null);


        String xml = weatherService.generateWeatherFragment(paramsStrani);

        assertTrue(xml.contains("cloudState=\"cloudy\""), "Dovrebbe riuscire a convertire la stringa '50' in double");
        assertTrue(xml.contains("intensity=\"0.0\""), "Null dovrebbe essere trattato come 0.0");
    }

    // Metodo Helper
    private Map<String, Object> creaMappa(Double cloudiness) {
        Map<String, Object> map = new HashMap<>();
        map.put("cloudiness", cloudiness);
        map.put("precipitation", 0.0);
        map.put("windIntensity", 0.0);
        map.put("sunAltitudeAngle", 0.0);
        return map;
    }
}