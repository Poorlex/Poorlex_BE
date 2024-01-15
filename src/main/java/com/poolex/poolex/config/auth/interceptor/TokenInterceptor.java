package com.poolex.poolex.config.auth.interceptor;

import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.token.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    //AntPatchMatcher 에서 숫자와 문자열을 구분할 수 없기에 따로 관리하는 정규식 모음
    private static final List<Pattern> EXCLUDE_PATTERNS = List.of(Pattern.compile("/battles/\\d+"));
    private static final String TOKEN_AUTHORIZATION_TYPE = "Bearer";

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RequestMemberInfo requestMemberInfo;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        final String pathInfo = request.getPathInfo();
        final boolean isHandleablePattern = EXCLUDE_PATTERNS.stream()
            .noneMatch(pattern -> pattern.matcher(pathInfo).matches());
        
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

    private String parseToken(final HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isEmpty(authorization) || !authorization.startsWith(TOKEN_AUTHORIZATION_TYPE)) {
            throw new IllegalArgumentException();
        }
        return authorization.substring(TOKEN_AUTHORIZATION_TYPE.length() + 1);
    }
}
