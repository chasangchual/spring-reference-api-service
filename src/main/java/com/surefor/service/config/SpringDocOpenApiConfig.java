package com.surefor.service.config;

import com.surefor.service.common.authentication.AuthenticationToken;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI springDocOpenApi() {

        Info info = new Info()
                .title("TMS Platform Service API")
                .version("1.0.0")
                .description("APIs for Heeartbeat Platform Services")
                .contact(new Contact().name("swIDch").url("https://swidch.com"));

        SecurityScheme jwtAuthorization = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name(AuthenticationToken.AUTHORIZATION_HEADER);


        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme(SecuritySchemeType.APIKEY.name())
                .in(SecurityScheme.In.HEADER).name(AuthenticationToken.API_KEY_HEADER);

        SecurityRequirement bearerAuthRequirement = new SecurityRequirement().addList("Bearer Auth");
        SecurityRequirement apiKeyRequirement = new SecurityRequirement().addList("API Key");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Auth", jwtAuthorization).addSecuritySchemes("API Key", apiKey))
                .security(List.of(bearerAuthRequirement, apiKeyRequirement))
                .servers(List.of(new Server().url("/").description("Default server")))
                .info(info);
    }
}