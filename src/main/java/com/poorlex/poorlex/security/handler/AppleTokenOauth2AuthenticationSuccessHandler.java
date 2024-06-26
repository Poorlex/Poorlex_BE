package com.poorlex.poorlex.security.handler;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.user.member.service.event.MemberRegisteredEvent;
import com.poorlex.poorlex.security.service.UserProfile;
import com.poorlex.poorlex.auth.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class AppleTokenOauth2AuthenticationSuccessHandler extends AbstractTokenOauth2AuthenticationSuccessHandler {

    private static final String REGISTRATION_ID = "APPLE";
    private static final String NICKNAME_KEY = "email";
    private static final String CLIENT_ID_KEY = "sub";

    public AppleTokenOauth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
                                                        final JwtTokenProvider jwtTokenProvider,
                                                        @Value("${url.server}") final String serverUrl) {
        super(memberRepository, jwtTokenProvider, serverUrl);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        log.info("success handler called: {}", authentication);
        final Member member = findOrCreateMember(authentication);
        final String accessToken = createToken(member.getId());
        final String uri = createURI(accessToken).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private Member findOrCreateMember(final Authentication authentication) {
        final UserProfile userProfile = (UserProfile) authentication.getPrincipal();
        final Oauth2RegistrationId registrationId = Oauth2RegistrationId.findByName(userProfile.getName());
        final String oauthId = userProfile.getAttribute(CLIENT_ID_KEY);

        return memberRepository.findByOauth2RegistrationIdAndOauthId(registrationId, oauthId)
                .orElseGet(() -> registerMember(userProfile, registrationId, oauthId));
    }

    private Member registerMember(final UserProfile userProfile,
                                  final Oauth2RegistrationId registrationId,
                                  final String oauthId) {
        final MemberNickname nickname = new MemberNickname(getNickname(userProfile));
        final Member newMember = memberRepository.save(Member.withoutId(registrationId, oauthId, nickname));

        Events.raise(new MemberRegisteredEvent(newMember.getId()));

        return newMember;
    }

    private String getNickname(final UserProfile userProfile) {
        final StringBuilder stringBuilder = new StringBuilder();

        final String email = userProfile.getAttribute(NICKNAME_KEY);
        String nickname;

        if (Objects.nonNull(email)) {
            nickname = email.split("@")[0];
        } else {
            nickname = SHORT_NICKNAME_PREFIX;
        }

        if (nickname.length() < NICKNAME_MINIMUM_LENGTH) {
            return stringBuilder.append(SHORT_NICKNAME_PREFIX).append(nickname).toString();
        }
        if (nickname.length() > NICKNAME_MAXIMUM_LENGTH) {
            return stringBuilder.append(nickname, 0, NICKNAME_MAXIMUM_LENGTH - 1).toString();
        }
        return nickname;
    }

    private URI createURI(final String accessToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("accessToken", accessToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(serverUrl)
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
