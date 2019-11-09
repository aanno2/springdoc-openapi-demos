package org.springdoc.demo.app1.config;

import org.springdoc.api.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class ConfigureOpenApi {

    @Bean
    public List<OpenApiCustomiser> openApiCustomiserList() {
        return Collections.emptyList();
    }
}
