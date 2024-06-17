package com.poorlex.poorlex.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import com.poorlex.poorlex.security.SecurityConfig;
import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected MemberRepository memberRepository;

    @BeforeEach
    protected void setUpUserDetails() {
        MemberInfo memberInfo = MemberInfo.ofUserRole(1L);
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(memberInfo, "ACCESS TOKEN", memberInfo.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(user);
    }

    protected void STUBBING_토큰에서_해당_회원ID를_추출하도록한다(final Long memberId) {
        MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(memberId);
        MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(memberId);
    }

    private void MOCKING_토큰이_회원_ID를_가진_클레임을_반환하도록_한다(final Long memberId) {
        final DefaultClaims 토큰_정보 = new DefaultClaims(Map.of("memberId", memberId));
        when(jwtTokenProvider.getPayload(any())).thenReturn(토큰_정보);
    }

    private void MOCKING_ID로_회원조회시_동일한_ID를_가지는_임의의_회원을_반환하도록_한다(final Long memberId) {
        final Member 스플릿 = new Member(memberId, Oauth2RegistrationId.APPLE, "고유 ID", new MemberNickname("스플릿"));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(스플릿));
    }
}
