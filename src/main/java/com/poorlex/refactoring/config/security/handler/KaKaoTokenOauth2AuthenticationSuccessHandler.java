package com.poorlex.refactoring.config.security.handler;

import com.poorlex.refactoring.config.jwt.JwtTokenProvider;
import com.poorlex.refactoring.config.security.service.UserProfile;
import com.poorlex.refactoring.user.member.domain.Member;
import com.poorlex.refactoring.user.member.domain.MemberNickname;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import com.poorlex.refactoring.user.member.domain.Oauth2RegistrationId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class KaKaoTokenOauth2AuthenticationSuccessHandler extends AbstractTokenOauth2AuthenticationSuccessHandler {

    private static final String REGISTRATION_ID = "KAKAO";
    private static final String ATTRIBUTE_ACCOUNT_KEY = "kakao_account";
    private static final String ATTRIBUTE_PROFILE_KEY = "profile";
    private static final String ATTRIBUTE_NICKNAME_KEY = "nickname";
    private static final String ATTRIBUTE_CLIENT_ID_KEY = "id";

    private final String redirectServerUrl;

    public KaKaoTokenOauth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
                                                        final JwtTokenProvider jwtTokenProvider,
                                                        final String redirectServerUrl) {
        super(memberRepository, jwtTokenProvider);
        this.redirectServerUrl = redirectServerUrl;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final Member member = findOrCreateMember(authentication);
        final String accessToken = createToken(member.getId());
        final String uri = createURI(accessToken).toString();
        log.info("token redirect url : {}", uri);

        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private Member findOrCreateMember(final Authentication authentication) {
        final UserProfile userProfile = (UserProfile) authentication.getPrincipal();
        final Oauth2RegistrationId registrationId = Oauth2RegistrationId.findByName(userProfile.getName());
        //String 으로 자동 맵핑이 되는지 테스트 필요
        final String oauthId = String.valueOf(userProfile.<Integer>getAttribute(ATTRIBUTE_CLIENT_ID_KEY));

        return memberRepository.findByOauth2RegistrationIdAndOauthId(registrationId, oauthId)
            .orElseGet(() -> memberRepository.save(
                Member.withoutId(registrationId, oauthId, new MemberNickname(getValidNickname(userProfile)))));
    }

    private String getValidNickname(final UserProfile principal) {
        final StringBuilder nicknameStringBuilder = new StringBuilder();

        final String nickname = principal.<Map<String, Map<String, String>>>getAttribute(ATTRIBUTE_ACCOUNT_KEY)
            .get(ATTRIBUTE_PROFILE_KEY)
            .get(ATTRIBUTE_NICKNAME_KEY);

        nicknameStringBuilder.append(nickname);

        if (nicknameStringBuilder.length() < NICKNAME_MINIMUM_LENGTH) {
            nicknameStringBuilder.insert(0, SHORT_NICKNAME_PREFIX);
        }
        if (nicknameStringBuilder.length() > NICKNAME_MAXIMUM_LENGTH) {
            nicknameStringBuilder.setLength(NICKNAME_MAXIMUM_LENGTH);
        }
        return nicknameStringBuilder.toString();
    }

    private URI createURI(final String accessToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("accessToken", accessToken);

        return UriComponentsBuilder
            .newInstance()
            .scheme("https")
            .host(redirectServerUrl)
            .path("/login/success")
            .queryParams(queryParams)
            .build()
            .toUri();
    }

    @Override
    public boolean supports(final String registrationId) {
        return REGISTRATION_ID.equalsIgnoreCase(registrationId);
    }
}
