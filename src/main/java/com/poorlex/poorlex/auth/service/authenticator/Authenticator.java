package com.poorlex.poorlex.auth.service.authenticator;

import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import org.springframework.stereotype.Component;

@Component
public interface Authenticator {

    String authenticate(String token);

    Oauth2RegistrationId provider();
}
