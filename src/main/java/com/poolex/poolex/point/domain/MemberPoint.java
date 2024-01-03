package com.poolex.poolex.point.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Point point;

    private Long memberId;

    public MemberPoint(final Long id, final Point point, final Long memberId) {
        this.id = id;
        this.point = point;
        this.memberId = memberId;
    }

    public static MemberPoint withoutId(final Point point, final Long memberId) {
        return new MemberPoint(null, point, memberId);
    }

    public static MemberPoint withId(final Long id, final Point point, final Long memberId) {
        return new MemberPoint(id, point, memberId);
    }

    public Long getId() {
        return id;
    }

    public int getPoint() {
        return point.getValue();
    }

    public Long getMemberId() {
        return memberId;
    }
}
