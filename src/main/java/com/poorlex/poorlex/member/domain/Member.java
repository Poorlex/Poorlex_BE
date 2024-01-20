package com.poorlex.poorlex.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, columnDefinition = "TEXT")
    private String oauthId;

    @Embedded
    private MemberNickname nickname;

    @Embedded
    private MemberDescription description;

    public Member(final Long id, final String oauthId, @NonNull final MemberNickname nickname) {
        this.id = id;
        this.oauthId = oauthId;
        this.nickname = nickname;
    }

    public static Member withoutId(final String oauthId, final MemberNickname nickname) {
        return new Member(null, oauthId, nickname);
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
