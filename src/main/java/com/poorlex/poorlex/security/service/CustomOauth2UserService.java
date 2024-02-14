package com.poorlex.poorlex.security.service;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String USER_ROLE = "ROLE_USER";

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        final OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return new UserProfile(
            registrationId,
            Collections.singleton(new SimpleGrantedAuthority(USER_ROLE)),
            oAuth2User.getAttributes()
        );
    }
}
