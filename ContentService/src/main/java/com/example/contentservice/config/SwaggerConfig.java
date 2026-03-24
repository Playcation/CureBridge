package com.example.contentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    servers = {
        @Server(url = "http://www.curebridge.site/api/content", description = "Gateway (Content Default)"),
        @Server(url = "http://www.curebridge.site/api/anonymous", description = "Gateway (Anonymous)"),
        @Server(url = "http://www.curebridge.site/api/user", description = "Gateway (User)"),
        @Server(url = "http://www.curebridge.site/api/admin", description = "Gateway (Admin)"),
        @Server(url = "http://www.curebridge.site", description = "Local Server (Direct)")
    }
)
public class SwaggerConfig {

}
