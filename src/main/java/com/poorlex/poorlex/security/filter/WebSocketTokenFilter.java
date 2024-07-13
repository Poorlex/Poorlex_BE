package com.poorlex.poorlex.security.filter;

import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketTokenFilter implements ChannelInterceptor {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (Objects.isNull(authorization)) {
                throw new AuthenticationCredentialsNotFoundException("JWT not found");
            }

            final String accessToken = authorization.split(" ")[1];
            final Long memberId = jwtTokenProvider.getPayload(accessToken, "memberId", Long.class);

            if (!memberRepository.existsById(memberId)) {
                throw new UsernameNotFoundException("user not found");
            }

            MemberInfo memberInfo = MemberInfo.ofUserRole(memberId);
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(memberInfo, accessToken, memberInfo.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(user);
            accessor.setUser(user);
        }
        return message;
    }
}
