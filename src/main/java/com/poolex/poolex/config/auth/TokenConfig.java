package com.poolex.poolex.config.auth;

import com.poolex.poolex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poolex.poolex.config.auth.interceptor.TokenInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class TokenConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final MemberArgumentResolver memberArgumentResolver;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
            .addPathPatterns("/battles/**")
            .addPathPatterns("/expenditures/**")
            .addPathPatterns("/points/**")
            .addPathPatterns("/weekly-budgets/**")
            .addPathPatterns("/alarm-reaction/**")
            .addPathPatterns("/goals");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
