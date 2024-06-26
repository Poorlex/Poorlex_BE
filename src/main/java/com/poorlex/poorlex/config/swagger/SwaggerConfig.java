package com.poorlex.poorlex.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.text.NumberFormat;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SwaggerConfig {

    private int order = 1;

    private final NumberFormat format = NumberFormat.getIntegerInstance();

    {
        format.setMinimumIntegerDigits(2);
    }

    @Bean
    @Profile("!dev")
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Poorlex REST API").description("푸얼렉스 API 문서"));
    }

    @Bean
    @Profile("dev")
    public OpenAPI devOpenAPI() {
        final Server server = new Server()
                .url("https://poorlex.com")
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
                .group(format.format(order++) + ". 전체")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi members() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 회원")
                .pathsToMatch("/member/**")
                .build();
    }

    @Bean
    public GroupedOpenApi points() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 포인트 및 레벨")
                .pathsToMatch("/point/**")
                .build();
    }

    @Bean
    public GroupedOpenApi weeklyBudgets() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 주간 예산")
                .pathsToMatch("/weekly-budgets/**")
                .build();
    }

    @Bean
    public GroupedOpenApi expenditures() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 지출")
                .pathsToMatch("/expenditures/**", "/battles/*/expenditures/**")
                .build();
    }

    @Bean
    public GroupedOpenApi battles() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 배틀")
                .pathsToMatch("/battles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi goals() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 목표")
                .pathsToMatch("/goals/**")
                .build();
    }

    @Bean
    public GroupedOpenApi alarms() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 알림")
                .pathsToMatch("/alarms/**")
                .build();
    }

    @Bean
    public GroupedOpenApi friends() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 친구")
                .pathsToMatch("/friends/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tests() {
        return GroupedOpenApi.builder()
                .group(format.format(order++) + ". 테스트")
                .pathsToMatch("/test/**")
                .build();
    }
}
