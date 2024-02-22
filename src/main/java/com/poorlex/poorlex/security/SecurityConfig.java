package com.poorlex.poorlex.security;

import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.security.coverter.AppleRequestEntityConverter;
import com.poorlex.poorlex.security.filter.JwtFilter;
import com.poorlex.poorlex.security.handler.AppleTokenOauth2AuthenticationSuccessHandler;
import com.poorlex.poorlex.security.handler.KaKaoTokenOauth2AuthenticationSuccessHandler;
import com.poorlex.poorlex.security.handler.Oauth2AuthenticationSuccessHandler;
import com.poorlex.poorlex.security.handler.TokenOauth2AuthenticationSuccessHandlerFacade;
import com.poorlex.poorlex.security.service.CustomOauth2UserService;
import com.poorlex.poorlex.token.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final String serverUrl;

    public SecurityConfig(final MemberRepository memberRepository,
                          final JwtTokenProvider jwtTokenProvider,
                          @Value("${url.server}") final String serverUrl) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.serverUrl = serverUrl;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureBaseAuthorization(http);
        configureAuthorizeRequests(http);
        configureOauth2Login(http);
        configureFilter(http);
        configureLogout(http);

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
        http.authorizeHttpRequests(oauth2 -> oauth2
                .anyRequest().permitAll()
//            .requestMatchers("/oauth2/login/**").permitAll()
//            .requestMatchers("/h2-console/**").permitAll()
//            .requestMatchers("/swagger-ui/**").permitAll()
//            .requestMatchers("/swagger/**").permitAll()
//            .requestMatchers("/api-docs/**").permitAll()
//            .requestMatchers("/battles").permitAll()
//            .requestMatchers(new RegexRequestMatcher("/battles/\\d+", HttpMethod.GET.name())).permitAll()
//            .requestMatchers("/login/success").permitAll()
//            .anyRequest().authenticated()
        );
    }

    private void configureOauth2Login(final HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2Login -> oauth2Login
            .tokenEndpoint(tokenEndpoint -> tokenEndpoint.accessTokenResponseClient(accessTokenResponseClient()))
            .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
            .successHandler(oAuth2AuthenticationSuccessHandler())
        );
    }

    private void configureFilter(final HttpSecurity http) {
        http.addFilterBefore(
            new JwtFilter(memberRepository, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class
        );
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOauth2UserService(jwtTokenProvider);
    }

    @Bean
    public Oauth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        final TokenOauth2AuthenticationSuccessHandlerFacade facade =
            new TokenOauth2AuthenticationSuccessHandlerFacade(memberRepository, jwtTokenProvider);

        facade.addHandlers(
            new KaKaoTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider, serverUrl));
        facade.addHandlers(
            new AppleTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider, serverUrl));

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
        accessTokenResponseClient.setRequestEntityConverter(new AppleRequestEntityConverter());

        return accessTokenResponseClient;
    }
}
