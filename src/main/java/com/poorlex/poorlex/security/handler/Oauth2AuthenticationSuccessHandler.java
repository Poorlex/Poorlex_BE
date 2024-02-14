package com.poorlex.poorlex.security.handler;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public interface Oauth2AuthenticationSuccessHandler extends AuthenticationSuccessHandler {

    boolean supports(final String registrationId);
}
