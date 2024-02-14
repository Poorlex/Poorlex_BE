package com.poorlex.poorlex.security;

import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.security.filter.JwtFilter;
import com.poorlex.poorlex.security.handler.KaKaoTokenOauth2AuthenticationSuccessHandler;
import com.poorlex.poorlex.security.handler.Oauth2AuthenticationSuccessHandler;
import com.poorlex.poorlex.security.handler.TokenOauth2AuthenticationSuccessHandlerFacade;
import com.poorlex.poorlex.security.service.CustomOauth2UserService;
import com.poorlex.poorlex.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

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
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    private void configureAuthorizeRequests(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(oauth2 -> oauth2
            .requestMatchers("/oauth2/login/**").permitAll()
            .requestMatchers("/battles").permitAll()
            .requestMatchers(new RegexRequestMatcher("/battles/\\d+", HttpMethod.GET.name())).permitAll()
            .requestMatchers("/login/success").permitAll()
            .anyRequest().authenticated()
        );
    }

    private void configureOauth2Login(final HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2Login -> oauth2Login
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
        return new CustomOauth2UserService();
    }

    @Bean
    public Oauth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        final TokenOauth2AuthenticationSuccessHandlerFacade facade =
            new TokenOauth2AuthenticationSuccessHandlerFacade(memberRepository, jwtTokenProvider);

        facade.addHandlers(new KaKaoTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider));
//        facade.addHandlers(new AppleTokenOauth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider));

        return facade;
    }

    private void configureLogout(final HttpSecurity http) throws Exception {
        http.logout(logoutConfigure -> logoutConfigure
            .logoutUrl("/logout")
            .clearAuthentication(true));
    }
}
