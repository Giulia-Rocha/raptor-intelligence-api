package com.ford.raptorapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI (springdoc):
 *  - metadados da API em /v3/api-docs
 *  - UI interativa em /swagger-ui.html
 *  - esquema de segurança "Bearer" (JWT) aplicado por padrão
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI raptorIntelligenceOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Ford Raptor Intelligence API")
                        .description("""
                                API de inteligência competitiva da picape Ford Ranger Raptor.

                                Autenticação: faça login em `POST /auth/login` (consultor: %s),
                                copie o `token` retornado e clique em "Authorize" preenchendo
                                `Bearer <token>` para acessar os recursos protegidos.
                                """.formatted("admin@ford.com.br / password"))
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().name("Ford Raptor Intelligence").email("raptor.api@ford.com.br")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}