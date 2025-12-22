package com.tirocinio.Scenario.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI scenarioServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scenario Service API")
                        .description("Microservizio per la gestione degli scenari di simulazione (selezione della Mappa) e configurazione dell'ambiente.")
                        .version("1.0.0"));
    }
}