package com.mo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gold Business Organisation Trust API")
                        .version("2026.1.0")
                        .description("API contract for organisation trust workflows: reviews, validation, commissions, guarantee claims, escrow and payment webhook integration.")
                        .contact(new Contact()
                                .name("Gold Business API Team")
                                .email("api-support@example.com")
                                .url("https://example.com/support"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
}
