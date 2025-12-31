package com.tirocinio.ConfigService.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ConfigService configService;

    @Test
    @DisplayName("getStatoSistema - Ritorna ATTIVO")
    void testGetStatoSistema() {
        String stato = configService.getStatoSistema();


        assertEquals("ATTIVO", stato);
    }

    @Test
    @DisplayName("inviaDatiAlGateway - Successo")
    void testInviaDatiAlGateway_Success() {
        String expectedResponse = "<Config>XML_DATA</Config>";
        when(restTemplate.postForObject(eq("http://localhost:8080/api/gateway"), any(), eq(String.class)))
                .thenReturn(expectedResponse);


        String result = configService.inviaDatiAlGateway();


        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("inviaDatiAlGateway - Errore Connessione (Catch Exception)")
    void testInviaDatiAlGateway_Failure() {
        String errorMsg = "Connection refused";
        when(restTemplate.postForObject(eq("http://localhost:8080/api/gateway"), any(), eq(String.class)))
                .thenThrow(new RestClientException(errorMsg));


        String result = configService.inviaDatiAlGateway();

        assertTrue(result.contains("Errore di connessione:"));
        assertTrue(result.contains(errorMsg));
    }
}