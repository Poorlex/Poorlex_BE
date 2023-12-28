package com.poolex.poolex.member.domain;

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
    @Embedded
    private MemberNickname nickname;

    public Member(final Long id, @NonNull final MemberNickname nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public static Member withoutId(final MemberNickname nickname) {
        return new Member(null, nickname);
    }

    public void changeNickname(final MemberNickname newNickname) {
        this.nickname = newNickname;
    }

    public Long getId() {
        return id;
    }
}
