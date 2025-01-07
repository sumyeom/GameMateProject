package com.example.gamemate.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("Game Mate")
                .version("v1.0.0")
                .description("Game Mate REST API");

        return new OpenAPI()
                .info(info);
    }
}