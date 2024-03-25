package com.poorlex.poorlex.config.auth;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poorlex.poorlex.config.auth.interceptor.TokenInterceptor;
import java.util.List;
import java.util.regex.Pattern;
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
        addExcludePattern(tokenInterceptor);
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/battles/**")
                .addPathPatterns("/battle-invite/**")
                .addPathPatterns("/expenditures/**")
                .addPathPatterns("/point/**")
                .addPathPatterns("/weekly-budgets/**")
                .addPathPatterns("/alarm-reaction/**")
                .addPathPatterns("/friends/**")
                .addPathPatterns("/member/**")
                .addPathPatterns("/goals/**")
                .addPathPatterns("/alarms/**")
                .excludePathPatterns("/goals/types");
    }

    private void addExcludePattern(final TokenInterceptor tokenInterceptor) {
        tokenInterceptor.addExcludePattern(ExcludePattern.withGet(Pattern.compile("/battles/\\d+")));
        tokenInterceptor.addExcludePattern(ExcludePattern.withGet(Pattern.compile("/goals/type")));
        tokenInterceptor.addExcludePattern(ExcludePattern.withGet(Pattern.compile("/battles")));
        tokenInterceptor.addExcludePattern(ExcludePattern.withGet(Pattern.compile("/expenditures/-?\\d+")));
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
    }
}
