package com.poorlex.poorlex.security.filter;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String accessToken = authorization.split(" ")[1];
            final Long memberId = jwtTokenProvider.getPayload(accessToken, "memberId", Long.class);

            if (!memberRepository.existsById(memberId)) {
                throw new UsernameNotFoundException("user not found");
            }

            MemberInfo memberInfo = MemberInfo.ofUserRole(memberId);
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(memberInfo, accessToken, memberInfo.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(user);
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
