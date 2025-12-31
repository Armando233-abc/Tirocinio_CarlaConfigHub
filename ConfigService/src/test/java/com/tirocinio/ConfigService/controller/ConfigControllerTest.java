package com.tirocinio.ConfigService.controller;

import com.tirocinio.ConfigService.service.ConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigController.class)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfigService configService;

    @Test
    @DisplayName("GET /Home - Mostra Dashboard e passa attributo al Model")
    void testMostraDashboard() throws Exception {
        when(configService.getStatoSistema()).thenReturn("ATTIVO");

        mockMvc.perform(get("/Home"))
                .andExpect(status().isOk())
                .andExpect(view().name("Home")) // Verifica il nome della vista ritornata
                .andExpect(model().attribute("statoHtml", "ATTIVO")); // Verifica che il model contenga i dati
    }

    @Test
    @DisplayName("POST /Home/genera - Ritorna la risposta del Gateway")
    void testAvviaGenerazione_Success() throws Exception {
        String gatewayResponse = "<FinalConfig>...</FinalConfig>";
        when(configService.inviaDatiAlGateway()).thenReturn(gatewayResponse);

        mockMvc.perform(post("/Home/genera"))
                .andExpect(status().isOk())
                .andExpect(content().string(gatewayResponse));
    }

    @Test
    @DisplayName("POST /Home/genera - Gestione Errore dal Service")
    void testAvviaGenerazione_Error() throws Exception {
        String errorResponse = "Errore di connessione: Connection refused";
        when(configService.inviaDatiAlGateway()).thenReturn(errorResponse);

        mockMvc.perform(post("/Home/genera"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Errore di connessione")));
    }
}