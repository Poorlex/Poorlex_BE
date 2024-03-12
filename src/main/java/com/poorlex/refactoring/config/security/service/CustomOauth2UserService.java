package com.poorlex.refactoring.config.security.service;

import com.poorlex.refactoring.config.jwt.JwtTokenProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String USER_ROLE = "ROLE_USER";
    private static final String APPLE_REGISTRATION_ID = "apple";
    private static final String APPLE_ID_TOKEN_PARAMETER_KEY_NAME = "id_token";

    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public CustomOauth2UserService(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!registrationId.equalsIgnoreCase(APPLE_REGISTRATION_ID)) {
            return new UserProfile(
                registrationId,
                Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
                defaultOAuth2UserService.loadUser(userRequest).getAttributes()
            );
        }
        return appleLoginUserProfile(userRequest, registrationId);
    }

    private UserProfile appleLoginUserProfile(final OAuth2UserRequest userRequest, final String registrationId) {
        final String idToken = userRequest.getAdditionalParameters()
            .get(APPLE_ID_TOKEN_PARAMETER_KEY_NAME)
            .toString();
        final Map<String, Object> idTokenPayloads = decodeIdTokenPayload(idToken);

        return new UserProfile(
            registrationId,
            Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
            idTokenPayloads
        );
    }

    public Map<String, Object> decodeIdTokenPayload(String idToken) {
        final AppleIdTokenPayload appleIdTokenPayload =
            jwtTokenProvider.decodePayload(idToken, AppleIdTokenPayload.class);
        final Map<String, Object> idTokenClaims = new HashMap<>();
        idTokenClaims.put("sub", appleIdTokenPayload.getSub());
        idTokenClaims.put("email", appleIdTokenPayload.getEmail());
        return idTokenClaims;
    }
}