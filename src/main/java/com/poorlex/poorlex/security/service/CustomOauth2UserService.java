package com.poorlex.poorlex.security.service;

import com.poorlex.poorlex.token.JwtTokenProvider;
import io.jsonwebtoken.Claims;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public CustomOauth2UserService(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equalsIgnoreCase(APPLE_REGISTRATION_ID)) {
            return appleLoginUserProfile(userRequest, registrationId);
        }
        return new UserProfile(
            registrationId,
            Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
            defaultOAuth2UserService.loadUser(userRequest).getAttributes()
        );
    }

    private UserProfile appleLoginUserProfile(final OAuth2UserRequest userRequest, final String registrationId) {
        final String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
        final Map<String, Object> attributes = decodeIdTokenPayload(idToken);
        attributes.put("id_token", idToken);
        return new UserProfile(
            registrationId,
            Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
            attributes
        );
    }

    public Map<String, Object> decodeIdTokenPayload(String idToken) {
        final Claims payload = jwtTokenProvider.getPayload(idToken);

        final Map<String, Object> idTokenClaims = new HashMap<>();
        idTokenClaims.put("iss", payload.getIssuer());
        idTokenClaims.put("iat", payload.getIssuedAt());
        idTokenClaims.put("exp", payload.getExpiration());
        idTokenClaims.put("aud", payload.getAudience());
        idTokenClaims.put("sub", payload.getSubject());
        idTokenClaims.put("email", payload.get("email", String.class));

        return idTokenClaims;
    }
}
