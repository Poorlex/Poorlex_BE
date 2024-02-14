package com.poorlex.poorlex.support.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableWebSecurity
public class MockMvcSpringSecurityTestConfigure {

    @Autowired
    protected WebApplicationContext context;

    @Bean
    public MockMvc mockMvc() {
        final MockMvcConfigurer configurer = SecurityMockMvcConfigurers.springSecurity();
        return MockMvcBuilders
            .webAppContextSetup(context)
            .apply(configurer)
            .build();
    }
}
