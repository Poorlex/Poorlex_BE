package com.poorlex.refactoring.config.security;

import com.poorlex.refactoring.config.jwt.JwtTokenProvider;
import com.poorlex.refactoring.config.security.handler.AppleTokenOauth2AuthenticationSuccessHandler;
import com.poorlex.refactoring.config.security.handler.KaKaoTokenOauth2AuthenticationSuccessHandler;
import com.poorlex.refactoring.config.security.handler.Oauth2AuthenticationSuccessHandler;
import com.poorlex.refactoring.config.security.handler.TokenOauth2AuthenticationSuccessHandlerFacade;
import com.poorlex.refactoring.config.security.requestconverter.AppleRequestEntityConverter;
import com.poorlex.refactoring.config.security.service.CustomOauth2UserService;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleRequestEntityConverter appleRequestEntityConverter;
    private final String redirectServerUrl;

    public SecurityConfiguration(final MemberRepository memberRepository,
                                 final JwtTokenProvider jwtTokenProvider,
                                 final AppleRequestEntityConverter appleRequestEntityConverter,
                                 @Value("${url.login-redirect}") final String redirectServerUrl) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.appleRequestEntityConverter = appleRequestEntityConverter;
        this.redirectServerUrl = redirectServerUrl;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .oauth2Login(oauth2Login -> oauth2Login
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenResponseClient(accessTokenResponseClient()))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                .successHandler(oAuth2AuthenticationSuccessHandler())
            )
            .authorizeHttpRequests(request -> request.anyRequest().permitAll())
            .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(appleRequestEntityConverter);

        return accessTokenResponseClient;
    }

    @Bean
    public Oauth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        final TokenOauth2AuthenticationSuccessHandlerFacade facade =
            new TokenOauth2AuthenticationSuccessHandlerFacade(memberRepository, jwtTokenProvider);

        facade.addHandlers(
            new KaKaoTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider, redirectServerUrl));
        facade.addHandlers(
            new AppleTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider, redirectServerUrl));

        return facade;
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOauth2UserService(jwtTokenProvider);
    }
}
