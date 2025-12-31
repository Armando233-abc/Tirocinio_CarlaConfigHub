package com.tirocinio.Composer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirocinio.Composer.service.ComposerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComposerController.class)
class ComposerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComposerService composerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /compose - 200 OK - XML generato correttamente")
    void testCompose_Success() throws Exception {
        Map<String, String> fragments = new HashMap<>();
        fragments.put("weatherFragment", "<Weather/>");
        fragments.put("vehicleFragment", "<Vehicle/>");

        String expectedFinalXml = "<OpenScenario>...COMPLETE...</OpenScenario>";


        when(composerService.composeConfiguration(anyMap())).thenReturn(expectedFinalXml);


        mockMvc.perform(post("/api/composer/compose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fragments)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedFinalXml));
    }

    @Test
    @DisplayName("POST /compose - 500 Internal Server Error - Errore durante l'assemblaggio")
    void testCompose_InternalError() throws Exception {
        Map<String, String> fragments = new HashMap<>();
        fragments.put("data", "test");

        when(composerService.composeConfiguration(anyMap()))
                .thenThrow(new RuntimeException("Errore parsing Regex"));

        mockMvc.perform(post("/api/composer/compose")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fragments)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Errore Composer: Errore parsing Regex"));
    }
}