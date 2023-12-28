package com.poolex.poolex.point.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Column(name = "point")
    private int value;

    public Point(final int value) {
        validation(value);
        this.value = value;
    }

    private void validation(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isGreaterOrEqualThan(final int targetPoint) {
        return this.value >= targetPoint;
    }

    public int getValue() {
        return value;
    }
}
