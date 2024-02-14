package com.poorlex.poorlex.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "member",
    indexes = {@Index(name = "idx_registrationId_oauthId", columnList = "oauth2RegistrationId, oauthId", unique = true)}
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Oauth2RegistrationId oauth2RegistrationId;

    @Column(unique = true, columnDefinition = "TEXT")
    private String oauthId;

    @Embedded
    private MemberNickname nickname;

    @Embedded
    private MemberDescription description;

    public Member(final Long id,
                  final Oauth2RegistrationId oauth2RegistrationId,
                  final String oauthId,
                  @NonNull final MemberNickname nickname) {
        this.id = id;
        this.oauth2RegistrationId = oauth2RegistrationId;
        this.oauthId = oauthId;
        this.nickname = nickname;
    }

    public static Member withoutId(final Oauth2RegistrationId oauth2RegistrationId,
                                   final String oauthId,
                                   final MemberNickname nickname) {
        return new Member(null, oauth2RegistrationId, oauthId, nickname);
    }

    public void changeNickname(final MemberNickname newNickname) {
        this.nickname = newNickname;
    }

    public void changeDescription(final MemberDescription description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Oauth2RegistrationId getOauth2RegistrationId() {
        return oauth2RegistrationId;
    }

    public String getOauthId() {
        return oauthId;
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description.getValue());
    }
}
