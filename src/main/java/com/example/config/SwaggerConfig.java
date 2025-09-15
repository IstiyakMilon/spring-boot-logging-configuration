package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("JSONPlaceholder Proxy API")
                        .description("Demo API that caches jsonplaceholder data and propagates correlation IDs")
                        .version("v1"));
    }

    @Bean
    public OpenApiCustomizer correlationHeaderCustomizer() {
        return openApi -> openApi.getPaths().forEach((path, item) -> {
            item.readOperations().forEach(operation -> {
                operation.addParametersItem(new HeaderParameter()
                        .name(CORRELATION_ID_HEADER)
                        .description("Optional correlation id propagated across requests")
                        .required(false));
            });
        });
    }
}
