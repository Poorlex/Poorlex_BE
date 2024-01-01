package com.poolex.poolex.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String oauthId;
    @Embedded
    private MemberNickname nickname;

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

    public String getOauthId() {
        return oauthId;
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public Long getId() {
        return id;
    }
}
