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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
public class KaKaoTokenOauth2AuthenticationSuccessHandler extends AbstractTokenOauth2AuthenticationSuccessHandler {

    private static final String REGISTRATION_ID = "KAKAO";
    private static final String ACCOUNT_KEY = "kakao_account";
    private static final String PROFILE_KEY = "profile";
    private static final String NICKNAME_KEY = "nickname";
    private static final String CLIENT_ID_KEY = "id";

    public KaKaoTokenOauth2AuthenticationSuccessHandler(final MemberRepository memberRepository,
                                                        final JwtTokenProvider jwtTokenProvider,
                                                        @Value("${url.server}") final String serverUrl) {
        super(memberRepository, jwtTokenProvider, serverUrl);
    }

    @Override
    @Transactional
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
        final String oauthId = String.valueOf(userProfile.<Integer>getAttribute(CLIENT_ID_KEY));

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

    private String getNickname(final UserProfile principal) {
        final StringBuilder stringBuilder = new StringBuilder();

        log.info("user info : {}", principal.<Map<String, Map<String, String>>>getAttribute(ACCOUNT_KEY));

        final String nickname = principal.<Map<String, Map<String, String>>>getAttribute(ACCOUNT_KEY)
                .get(PROFILE_KEY)
                .get(NICKNAME_KEY);

        if (Objects.isNull(nickname)) {
            stringBuilder.append(SHORT_NICKNAME_PREFIX).append(UUID.randomUUID())
                    .setLength(NICKNAME_MAXIMUM_LENGTH);
            return stringBuilder.toString();
        }

        if (nickname.length() < NICKNAME_MINIMUM_LENGTH) {
            return stringBuilder.append(SHORT_NICKNAME_PREFIX).append(nickname).toString();
        }
        if (nickname.length() > NICKNAME_MAXIMUM_LENGTH) {
            return stringBuilder.append(nickname, 0, NICKNAME_MAXIMUM_LENGTH).toString();
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
