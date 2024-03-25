package com.poorlex.poorlex.user.point.domain;

import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class PointTest implements ReplaceUnderScoreTest {

    @Test
    void 포인트가_음수일_경우_예외를_던진다() {
        //given
        final int 포인트_값 = -1;

        //when
        //then
        assertThatThrownBy(() -> new Point(포인트_값))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 포인트보다_크거나_같은지를_반환한다() {
        //given
        final Point 포인트 = new Point(1);

        //when
        final int 포인트보다_작은값 = 0;
        final int 포인트랑_동일한_값 = 1;
        final int 포인트보다_큰값 = 2;

        //then
        assertThat(포인트.isGreaterOrEqualThan(포인트보다_작은값)).isTrue();
        assertThat(포인트.isGreaterOrEqualThan(포인트랑_동일한_값)).isTrue();
        assertThat(포인트.isGreaterOrEqualThan(포인트보다_큰값)).isFalse();
    }
}
