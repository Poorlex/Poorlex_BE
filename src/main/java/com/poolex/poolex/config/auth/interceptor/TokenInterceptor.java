package com.poolex.poolex.config.auth.interceptor;

import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.token.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_AUTHORIZATION_TYPE = "Bearer";

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RequestMemberInfo requestMemberInfo;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        final String token = parseToken(request);
        final Claims payload = tokenProvider.getPayload(token);
        final Long memberId = payload.get("memberId", Long.class);
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(IllegalArgumentException::new);
        requestMemberInfo.setMemberId(member.getId());
        return true;
    }

    private String parseToken(final HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isEmpty(authorization) || !authorization.startsWith(TOKEN_AUTHORIZATION_TYPE)) {
            throw new IllegalArgumentException();
        }
        return authorization.substring(TOKEN_AUTHORIZATION_TYPE.length() + 1);
    }
}
