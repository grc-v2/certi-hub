package com.certimaster.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI certiMasterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CertiMaster API")
                        .version("v1")
                        .description("""
                                REST API for CertiMaster — a certification learning & exam-practice platform.

                                **Authentication**: JWT Bearer token. Obtain via `POST /api/v1/auth/login`
                                and pass as `Authorization: Bearer <token>` header. Tokens are RSA-signed
                                (RS256) and verified by the OAuth2 resource server.

                                **Response envelope**: successful responses are wrapped as
                                `{ metadata, data }`. Error responses follow RFC 7807 `ProblemDetail`.
                                """)
                        .contact(new Contact()
                                .name("CertiMaster Team")
                                .url("https://github.com/tanvx"))
                        .license(new License()
                                .name("Educational use")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local dev"),
                        new Server().url("https://api.certimaster.com").description("Production")
                ))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the access token returned by /api/v1/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    @Bean
    public GroupedOpenApi identityGroup() {
        return GroupedOpenApi.builder()
                .group("1-identity")
                .displayName("Identity & Auth")
                .packagesToScan("com.certimaster.identity.adapter.web")
                .build();
    }

    @Bean
    public GroupedOpenApi learningGroup() {
        return GroupedOpenApi.builder()
                .group("2-learning")
                .displayName("Learning (Certifications, Exams)")
                .packagesToScan("com.certimaster.learning.adapter.web")
                .build();
    }

    @Bean
    public GroupedOpenApi engagementGroup() {
        return GroupedOpenApi.builder()
                .group("3-engagement")
                .displayName("Engagement (Posts, Bookmarks, Notifications)")
                .packagesToScan("com.certimaster.engagement.adapter.web")
                .build();
    }

    @Bean
    public GroupedOpenApi lookupGroup() {
        return GroupedOpenApi.builder()
                .group("4-lookup")
                .displayName("Lookup (Reference data)")
                .packagesToScan("com.certimaster.lookup.adapter")
                .build();
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("0-all")
                .displayName("All endpoints")
                .pathsToMatch("/api/**")
                .build();
    }
}
