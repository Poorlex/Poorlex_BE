package com.poorlex.poorlex.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {

    private int order = 1;

    @Value("${url.server}")
    private String serverUrl;

    @Bean
    @Profile("!dev")
    public OpenAPI openAPI() {
        final Server local = new Server()
                .url("http://" + serverUrl)
                .description("for local API call");

        return new OpenAPI()
                .servers(List.of(local))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Poorlex REST API").description("푸얼렉스 API 문서"));
    }

    @Bean
    @Profile("dev")
    public OpenAPI devOpenAPI() {
        final Server server = new Server()
                .url("https://" + serverUrl)
                .description("for real API call");

        return new OpenAPI()
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Poorlex REST API").description("푸얼렉스 API 문서"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public GroupedOpenApi all() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 전체")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi members() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 회원")
                .pathsToMatch("/member/**")
                .build();
    }

    @Bean
    public GroupedOpenApi points() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 포인트 및 레벨")
                .pathsToMatch("/point/**")
                .build();
    }

    @Bean
    public GroupedOpenApi weeklyBudgets() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 주간 예산")
                .pathsToMatch("/weekly-budgets/**")
                .build();
    }

    @Bean
    public GroupedOpenApi expenditures() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 지출")
                .pathsToMatch("/expenditures/**", "/battles/*/expenditures/**")
                .build();
    }

    @Bean
    public GroupedOpenApi battles() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 배틀")
                .pathsToMatch("/battles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi goals() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 목표")
                .pathsToMatch("/goals/**")
                .build();
    }

    @Bean
    public GroupedOpenApi alarms() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 알림")
                .pathsToMatch("/alarms/**")
                .build();
    }

    @Bean
    public GroupedOpenApi friends() {
        return GroupedOpenApi.builder()
                .group(order++ + ". 친구")
                .pathsToMatch("/friends/**")
                .build();
    }
}
