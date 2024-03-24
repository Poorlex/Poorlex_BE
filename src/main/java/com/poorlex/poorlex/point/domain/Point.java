package com.poorlex.poorlex.point.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
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
            final String errorMessage = String.format("포인트는 음수일 수 없습니다. ( 포인트 : %d )", value);
            throw new ApiException(ExceptionTag.MEMBER_POINT, errorMessage);
        }
    }

    public boolean isGreaterOrEqualThan(final int targetPoint) {
        return this.value >= targetPoint;
    }

    public int getValue() {
        return value;
    }
}
