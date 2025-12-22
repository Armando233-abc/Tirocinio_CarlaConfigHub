package com.tirocinio.Composer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI vehicleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vehicle Service API")
                        .description("Microservizio responsabile dell'aggregazione dei frammenti XML (Meteo, Veicolo, Scenario) e della validazione sintattica della configurazione finale.")
                        .version("1.0.0"));
    }
}