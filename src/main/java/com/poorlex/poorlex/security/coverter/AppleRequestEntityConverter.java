package com.poorlex.poorlex.security.coverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

@Slf4j
public class AppleRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    @Value("${apple.audience}")
    private String APPLE_AUDIENCE;

    @Value("${apple.private-key}")
    private String APPLE_KEY;

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${apple.key-id}")
    private String APPLE_KEY_ID;
    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        final RequestEntity<?> entity = defaultConverter.convert(req);
        final String registrationId = req.getClientRegistration().getRegistrationId();
        final MultiValueMap<String, Object> params = (MultiValueMap<String, Object>) entity.getBody();
        log.info("registrationId : {}", registrationId);
        log.info("client secret : {}", createClientSecret());
        if (registrationId.equalsIgnoreCase("apple")) {
            params.set("client_secret", createClientSecret());
        }
        return new RequestEntity<>(params, entity.getHeaders(), entity.getMethod(), entity.getUrl());
    }

    public String createClientSecret() {
        final Date expirationDate = Date.from(
            LocalDateTime.now()
                .plusDays(30)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );

        return Jwts.builder()
            .setHeaderParams(appleJwtHeader())
            .setIssuer(APPLE_TEAM_ID)
            .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
            .setExpiration(expirationDate) // 만료 시간
            .setAudience(APPLE_AUDIENCE)
            .setSubject(APPLE_CLIENT_ID)
            .signWith(getPrivateKey())
            .compact();
    }

    private Map<String, Object> appleJwtHeader() {
        final Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", APPLE_KEY_ID);
        jwtHeader.put("alg", "ES256");
        return jwtHeader;
    }

    public Key getPrivateKey() {
        return Keys.hmacShaKeyFor(APPLE_KEY.getBytes());
    }
}
