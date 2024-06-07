package com.poorlex.poorlex.security;

import com.poorlex.poorlex.security.coverter.AppleRequestEntityConverter;
import com.poorlex.poorlex.security.filter.JwtFilter;
import com.poorlex.poorlex.security.handler.*;
import com.poorlex.poorlex.security.service.CustomOauth2UserService;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOauth2UserService oAuth2UserService;
    private final AppleRequestEntityConverter appleRequestEntityConverter;
    private final KaKaoTokenOauth2AuthenticationSuccessHandler kaKaoTokenOauth2AuthenticationSuccessHandler;
    private final AppleTokenOauth2AuthenticationSuccessHandler appleTokenOauth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureBaseAuthorization(http);
        configureAuthorizeRequests(http);
        configureOauth2Login(http);
        configureLogout(http);
        configureFilter(http);

        return http.build();
    }

    private void configureBaseAuthorization(final HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> {
                          try {
                              csrf.disable()
                                      .headers(headers -> headers
                                              .frameOptions(FrameOptionsConfig::disable));
                          } catch (Exception e) {
                              e.printStackTrace();
                              throw new RuntimeException(e);
                          }
                      }
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);
    }

    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void configureAuthorizeRequests(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(oauth2 -> oauth2.anyRequest().permitAll());
    }

    private void configureOauth2Login(final HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2Login -> oauth2Login
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenResponseClient(accessTokenResponseClient()))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler())
        );
    }

    private void configureFilter(final HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(memberRepository, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class
        );
    }

    @Bean
    public Oauth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        final TokenOauth2AuthenticationSuccessHandlerFacade facade =
                new TokenOauth2AuthenticationSuccessHandlerFacade(memberRepository, jwtTokenProvider);

        facade.addHandlers(kaKaoTokenOauth2AuthenticationSuccessHandler);
        facade.addHandlers(appleTokenOauth2AuthenticationSuccessHandler);

        return facade;
    }

    private void configureLogout(final HttpSecurity http) throws Exception {
        http.logout(logoutConfigure -> logoutConfigure
                .logoutUrl("/logout")
                .clearAuthentication(true));
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(appleRequestEntityConverter);

        return accessTokenResponseClient;
    }
}
