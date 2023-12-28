package com.poolex.poolex.point.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;

class PointTest implements ReplaceUnderScoreTest {

    @Test
    void 포인트가_음수일_경우_예외를_던진다() {
        //given
        final int pointValue = -1;

        //when
        //then
        assertThatThrownBy(() -> new Point(pointValue))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 포인트보다_크거나_같은지를_반환한다() {
        //given
        final Point point = new Point(1);

        //when
        final boolean biggerValueResult = point.isGreaterOrEqualThan(2);
        final boolean smallValueResult = point.isGreaterOrEqualThan(0);

        //then
        assertThat(biggerValueResult).isFalse();
        assertThat(smallValueResult).isTrue();
    }
}
