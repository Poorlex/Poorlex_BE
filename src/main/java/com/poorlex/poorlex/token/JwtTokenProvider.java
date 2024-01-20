package com.poorlex.poorlex.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
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
            throw new IllegalArgumentException();
        }
    }
}
