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
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class AppleTokenOauth2AuthenticationSuccessHandler extends AbstractTokenOauth2AuthenticationSuccessHandler {

    private static final String REGISTRATION_ID = "APPLE";
    private static final String ATTRIBUTE_NICKNAME_KEY = "email";
    private static final String ATTRIBUTE_CLIENT_ID_KEY = "sub";
    private static final String EMAIL_DELIMITER = "@";
    private final String redirectServerUrl;

    public AppleTokenOauth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
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
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private Member findOrCreateMember(final Authentication authentication) {
        final UserProfile userProfile = (UserProfile) authentication.getPrincipal();
        final Oauth2RegistrationId registrationId = Oauth2RegistrationId.findByName(userProfile.getName());
        final String oauthId = userProfile.getAttribute(ATTRIBUTE_CLIENT_ID_KEY);

        return memberRepository.findByOauth2RegistrationIdAndOauthId(registrationId, oauthId)
            .orElseGet(() -> memberRepository.save(
                Member.withoutId(registrationId, oauthId, new MemberNickname(getNickname(userProfile)))));
    }

    private String getNickname(final UserProfile userProfile) {
        final StringBuilder nicknameStringBuilder = new StringBuilder();

        final String email = userProfile.getAttribute(ATTRIBUTE_NICKNAME_KEY);
        final String nickname = email.split(EMAIL_DELIMITER)[0];

        nicknameStringBuilder.append(nickname);

        if (nicknameStringBuilder.length() < NICKNAME_MINIMUM_LENGTH) {
            nicknameStringBuilder.insert(0, SHORT_NICKNAME_PREFIX);
        }
        if (nicknameStringBuilder.length() > NICKNAME_MAXIMUM_LENGTH) {
            nicknameStringBuilder.setLength(NICKNAME_MAXIMUM_LENGTH);
        }

        return nickname;
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
