package com.tirocinio.GatewayService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI carlaConfigHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gateway Service API")
                        .description("API Gateway per l'orchestrazione di CarlaConfigHub")
                        .version("1.0.0"));
    }
}