package com.tirocinio.ConfigService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // Attivo solo in sviluppo
public class OpenApiConfig {

    @Bean
    public OpenAPI configServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Config Service API")
                        .description("Microservizio Core per la gestione della simulazione. Consente di definire e strutturare i parametri principali (Ambiente, Veicoli, Scenario)")
                        .version("1.0.0"));
    }
}