package com.gelatoflow.gelatoflow_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "GelatoFlow API",
                version     = "v1",
                description = "REST API aplikacji do zarządzania siecią lodziarni",
                contact     = @Contact(name = "Team", email = "team@gelatoflow.com"),
                license     = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
        )
)
public class OpenApiConfig {
}
