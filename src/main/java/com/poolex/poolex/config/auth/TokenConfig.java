package com.poolex.poolex.config.auth;

import com.poolex.poolex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poolex.poolex.config.auth.interceptor.RequestMemberInfo;
import com.poolex.poolex.config.auth.interceptor.TokenInterceptor;
import com.poolex.poolex.login.domain.MemberRepository;
import com.poolex.poolex.token.JwtTokenProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class TokenConfig implements WebMvcConfigurer {

    private final JwtTokenProvider tokenProvider;

    private final MemberRepository memberRepository;

    private final RequestMemberInfo requestMemberInfo;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor())
            .addPathPatterns("/battles/**")
            .addPathPatterns("/expenditures/**");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver());
    }

    private TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor(tokenProvider, memberRepository, requestMemberInfo);
    }

    private MemberArgumentResolver memberArgumentResolver() {
        return new MemberArgumentResolver(requestMemberInfo);
    }
}
