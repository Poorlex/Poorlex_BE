package com.poorlex.poorlex.security.coverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@Slf4j
public class AppleRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    @Value("${apple.audience}")
    private String appleAudience;

    @Value("${apple.private-key}")
    private String privateKey;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;
    private final OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        final RequestEntity<?> entity = defaultConverter.convert(req);
        final String registrationId = req.getClientRegistration().getRegistrationId();
        final MultiValueMap<String, Object> params = (MultiValueMap<String, Object>) entity.getBody();
        log.info("registrationId : {}", registrationId);
        if (registrationId.equalsIgnoreCase("apple")) {
            params.set("client_secret", createClientSecret());
        }
        for (Entry<String, List<Object>> stringListEntry : params.entrySet()) {
            log.info("request entity paramter \"{}\" : {}", stringListEntry.getKey(), stringListEntry.getValue());
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
            .setIssuer(teamId)
            .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
            .setExpiration(expirationDate) // 만료 시간
            .setAudience(appleAudience)
            .setSubject(clientId)
            .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
            .compact();
    }

    private Map<String, Object> appleJwtHeader() {
        final Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("alg", "ES256");
        jwtHeader.put("kid", keyId);
        return jwtHeader;
    }

    private PrivateKey getPrivateKey() {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            exception.printStackTrace();
            throw new IllegalArgumentException("Apple secretkey 생성에 실패하여습니다.");
        }
    }
}
