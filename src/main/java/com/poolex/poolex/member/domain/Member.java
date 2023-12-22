package com.poolex.poolex.member.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private MemberNickname nickname;

    @Embedded
    private MemberPoint point;

    public Member(final Long id, final MemberNickname nickname, final MemberPoint point) {
        this.id = id;
        this.nickname = nickname;
        this.point = point;
    }

    public static Member withoutId(final MemberNickname nickname, final MemberPoint point) {
        return new Member(null, nickname, point);
    }

    public void changeNickname(final MemberNickname newNickname) {
        this.nickname = newNickname;
    }

    public void addPoint(final int additionalPoint) {
        point.addPoint(additionalPoint);
    }

    public Long getId() {
        return id;
    }

    public MemberLevel getLevel() {
        return point.getLevel();
    }
}
