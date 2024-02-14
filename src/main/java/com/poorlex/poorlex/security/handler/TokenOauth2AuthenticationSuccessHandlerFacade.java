package com.poorlex.poorlex.security.handler;

import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.security.service.UserProfile;
import com.poorlex.poorlex.token.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class TokenOauth2AuthenticationSuccessHandlerFacade extends AbstractTokenOauth2AuthenticationSuccessHandler {

    private final List<AbstractTokenOauth2AuthenticationSuccessHandler> handlers = new ArrayList<>();

    public TokenOauth2AuthenticationSuccessHandlerFacade(final MemberRepository memberRepository,
                                                         final JwtTokenProvider jwtTokenProvider) {
        super(memberRepository, jwtTokenProvider);
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof OidcUser) {
            throw new IllegalArgumentException("OpenID Connect 방식은 지원하지 않습니다.");
        }
        final String registrationId = ((UserProfile) authentication.getPrincipal()).getName();
        final AbstractTokenOauth2AuthenticationSuccessHandler validHandler = findValidHandler(registrationId);
        validHandler.onAuthenticationSuccess(request, response, authentication);
    }

    private AbstractTokenOauth2AuthenticationSuccessHandler findValidHandler(final String registrationId) {
        return handlers.stream()
            .filter(handler -> handler.supports(registrationId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException(
                "지원하지 않는 Oauth2.0 Registration Id 입니다. ( id = " + registrationId + " )"));
    }

    @Override
    public boolean supports(final String registrationId) {
        return handlers.stream()
            .anyMatch(handler -> handler.supports(registrationId));
    }

    public void addHandlers(final AbstractTokenOauth2AuthenticationSuccessHandler handler) {
        handlers.add(handler);
    }
}
