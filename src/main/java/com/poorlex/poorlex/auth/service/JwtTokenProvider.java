package com.poorlex.poorlex.auth.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final long accessTokenExpireTimeInMilli;
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.token.secret-key}") final String secretKey,
                            @Value("${jwt.token.access-expire-length}") final long accessTokenExpireTimeInMilli) {
        this.accessTokenExpireTimeInMilli = accessTokenExpireTimeInMilli;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(final Long memberId) {
        final Date now = new Date();

        final Claims claims = Jwts.claims();
        claims.setIssuedAt(now);
        claims.setExpiration(new Date(now.getTime() + accessTokenExpireTimeInMilli));
        claims.put("memberId", memberId);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    public Claims getPayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid JWT token");
        }
    }

    public <T> T getPayload(final String token, final String claimName, final Class<T> type) {
        return getPayload(token).get(claimName, type);
    }

    public <T> T decodePayload(String token, Class<T> targetClass) {

        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error decoding token payload", e);
        }
    }
}
