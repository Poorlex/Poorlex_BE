package com.poorlex.poorlex.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.auth.service.dto.request.LoginRequest;
import com.poorlex.poorlex.auth.service.dto.response.LoginTokenResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import com.poorlex.poorlex.token.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    private static String SECRET_KEY = "testtokensecretkeytesttokensecretkeytesttokensecretkey";
    private static int ACCESS_EXPIRE_LENGTH = 3600000;

    @Autowired
    private MemberRepository memberRepository;
    private JwtTokenProvider tokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        this.tokenProvider = new JwtTokenProvider(SECRET_KEY, ACCESS_EXPIRE_LENGTH);
        this.authService = new AuthService(memberRepository, tokenProvider);
    }

    @Test
    void 기존에_가입된_멤버가_로그인을_진행한다() {
        //given
        final Member member = createMember("oauthId", "nickname");
        final LoginRequest request = new LoginRequest(member.getOauthId(), member.getNickname());

        //when
        final LoginTokenResponse loginTokenResponse = authService.loginAfterRegisterIfNotExist(request);

        //then
        final String createdToken = loginTokenResponse.getAccessToken();
        final Long tokenMemberId = tokenProvider.getPayload(createdToken)
            .get("memberId", Long.class);

        assertThat(tokenMemberId).isEqualTo(member.getId());
    }

    @Test
    void 가입되어_있지않은_멤버가_로그인을_진행한다() {
        //given
        memberRepository.deleteAll();
        final LoginRequest request = new LoginRequest("newOauthId", "newNickname");

        //when
        final LoginTokenResponse loginTokenResponse = authService.loginAfterRegisterIfNotExist(request);

        //then
        final Member expectedMember = Member.withoutId(request.getOauthId(), new MemberNickname(request.getNickname()));
        final Optional<Member> member = memberRepository.findByOauthId(request.getOauthId());
        assertSoftly(
            softly -> {
                softly.assertThat(member).isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedMember);
                softly.assertThat(loginTokenResponse.getAccessToken()).isNotBlank();
            }
        );
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname(nickname)));
    }
}
