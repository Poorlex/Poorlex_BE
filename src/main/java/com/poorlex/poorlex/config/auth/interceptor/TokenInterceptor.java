package com.poorlex.poorlex.config.auth.interceptor;

import com.poorlex.poorlex.config.auth.ExcludePattern;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.token.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_AUTHORIZATION_TYPE = "Bearer";

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RequestMemberInfo requestMemberInfo;
    private final List<ExcludePattern> excludePatterns = new ArrayList<>();

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        final String requestURI = request.getRequestURI();
        final boolean isHandleablePattern = excludePatterns.stream()
            .noneMatch(excludePattern -> excludePattern.matches(requestURI, HttpMethod.valueOf(request.getMethod())));

        if (isHandleablePattern) {
            final String token = parseToken(request);
            final Claims payload = tokenProvider.getPayload(token);
            final Long memberId = payload.get("memberId", Long.class);
            final Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
            requestMemberInfo.setMemberId(member.getId());
        }
        return true;
    }

    public void addExcludePattern(final ExcludePattern excludePattern) {
        this.excludePatterns.add(excludePattern);
    }

    private String parseToken(final HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isEmpty(authorization) || !authorization.startsWith(TOKEN_AUTHORIZATION_TYPE)) {
            throw new IllegalArgumentException();
        }
        return authorization.substring(TOKEN_AUTHORIZATION_TYPE.length() + 1);
    }
}
