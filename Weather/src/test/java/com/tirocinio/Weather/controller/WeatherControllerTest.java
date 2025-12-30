package com.tirocinio.Weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirocinio.Weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest carica SOLO il controller e non tutta l'applicazione. È molto veloce.
@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. TEST SUCCESSO (200)
    @Test
    void testGenerateWeather_Success() throws Exception {
        String fakeXml = "<Weather>Sunny</Weather>";
        when(weatherService.generateWeatherFragment(any())).thenReturn(fakeXml);


        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("cloudiness", 10.0);


        mockMvc.perform(post("/api/weather/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParams)))
                .andExpect(status().isOk())
                .andExpect(content().string(fakeXml));
    }

    // 2. TEST ERRORE (400)
    @Test
    void testGenerateWeather_BadRequest() throws Exception {
        when(weatherService.generateWeatherFragment(any()))
                .thenThrow(new IllegalArgumentException("Il parametro 'cloudiness' è sbagliato"));

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("cloudiness", 150.0);


        mockMvc.perform(post("/api/weather/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParams)))
                .andExpect(status().isBadRequest()) .andExpect(content().string(org.hamcrest.Matchers.containsString("Errore Validazione Weather")));
    }

    // 3. TEST ERRORE INTERNO (500)
    @Test
    void testGenerateWeather_InternalError() throws Exception {
        when(weatherService.generateWeatherFragment(any()))
                .thenThrow(new RuntimeException("Crash imprevisto!"));

        Map<String, Object> requestParams = new HashMap<>();

        mockMvc.perform(post("/api/weather/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParams)))
                .andExpect(status().isInternalServerError()) // Mi aspetto 500
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Errore Interno Weather Service")));
    }
}