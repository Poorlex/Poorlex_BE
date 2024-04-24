package com.poorlex.poorlex.security.service;

import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        log.info("::::::::: loadUser Called ::::::::::");
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("::::::::: registrationId : {} ::::::::", registrationId);
        if (registrationId.equalsIgnoreCase(APPLE_REGISTRATION_ID)) {
            log.info("::::::::: apple login user called ::::::::");
            return appleLoginUserProfile(userRequest, registrationId);
        }
        return new UserProfile(
                registrationId,
                Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
                defaultOAuth2UserService.loadUser(userRequest).getAttributes()
        );
    }

    private UserProfile appleLoginUserProfile(final OAuth2UserRequest userRequest, final String registrationId) {
        final String idToken = userRequest.getAdditionalParameters()
                .get("id_token")
                .toString();
        final Map<String, Object> idTokenPayloads = decodeIdTokenPayload(idToken);
        log.info("idTokenPayloads : {}", idTokenPayloads);

        return new UserProfile(
                registrationId,
                Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
                idTokenPayloads
        );
    }

    public Map<String, Object> decodeIdTokenPayload(String idToken) {
        final AppleIdTokenPayload appleIdTokenPayload =
                jwtTokenProvider.decodePayload(idToken, AppleIdTokenPayload.class);
        log.info("sub : {}", appleIdTokenPayload.getSub());
        final Map<String, Object> idTokenClaims = new HashMap<>();
        idTokenClaims.put("sub", appleIdTokenPayload.getSub());
        idTokenClaims.put("email", appleIdTokenPayload.getEmail());
        return idTokenClaims;
    }
}
