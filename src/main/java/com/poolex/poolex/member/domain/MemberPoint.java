package com.poolex.poolex.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPoint {

    @Column(name = "point")
    private int value;

    public MemberPoint(final int value) {
        validation(value);
        this.value = value;
    }

    private void validation(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isGreaterOrEqualThan(final MemberPoint target) {
        return this.value >= target.value;
    }

    public void addPoint(final int additionalPoint) {
        validation(additionalPoint);
        value += additionalPoint;
    }

    public MemberLevel getLevel() {
        return MemberLevel.findByMemberPoint(this)
            .orElseThrow(IllegalArgumentException::new);
    }
}
