package com.davison.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Task Manager API",
        version = "1.0",
        description = "API para gerenciamento de tarefas pessoais com autenticação JWT",
        contact = @Contact(
            name = "Task Manager Support",
            email = "support@taskmanager.com"
        )
    ),
    servers = @Server(
        url = "http://localhost:8080",
        description = "Servidor de desenvolvimento"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token JWT obtido através do endpoint /auth/login"
)
public class OpenApiConfig {
}