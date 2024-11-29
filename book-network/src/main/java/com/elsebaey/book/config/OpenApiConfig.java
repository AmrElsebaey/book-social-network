package com.elsebaey.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
                name = "Amr Elsebaey",
                email = "amrelsebay3@gmail.com"
        ),
            description = "OpenApi documentation for Spring security",
            title = "OpenApi specification - Elsebaey",
            version = "1.0.0",
            license = @License(
                    name = "License name",
                    url = "https://some-url.com"
            ),
            termsOfService = "Terms of service"
    ),
        servers = {
            @Server(
                    description = "Local Environment",
                    url = "http://localhost:8088/api/v1"
            ),
            @Server(
                    description = "Production Environment",
                    url = "https://elsebaey.com/api/v1"
            )
        },
        security = {
            @SecurityRequirement(
                    name = "bearerAuth"
            )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
