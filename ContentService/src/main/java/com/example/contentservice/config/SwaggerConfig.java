package com.example.contentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    servers = {
        @Server(url = "http://localhost:8080/api/content", description = "Gateway (Content Default)"),
        @Server(url = "http://localhost:8080/api/anonymous", description = "Gateway (Anonymous)"),
        @Server(url = "http://localhost:8080/api/user", description = "Gateway (User)"),
        @Server(url = "http://localhost:8080/api/admin", description = "Gateway (Admin)"),
        @Server(url = "http://localhost:8082", description = "Local Server (Direct)")
    }
)
public class SwaggerConfig {
}
