package com.poorlex.poorlex.config.auth;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poorlex.poorlex.config.auth.interceptor.TokenInterceptor;
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
            .addPathPatterns("/friends/**")
            .addPathPatterns("/member/**")
            .addPathPatterns("/goals/**")
            .excludePathPatterns("/goals/types");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
