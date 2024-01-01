package com.poolex.poolex.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.point.domain.Point;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberLevelTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "포인트가 {0}일 때")
    @CsvSource(value = {
        "1440:LEVEL_5",
        "1439:LEVEL_4",
        "599:LEVEL_3",
        "189:LEVEL_2",
        "69:LEVEL_1",
    }, delimiter = ':')
    void 포인트에_따른_레벨을_찾는다(final int pointValue, final MemberLevel expectedLevel) {
        //given
        final Point point = new Point(pointValue);

        //when
        final MemberLevel memberLevel = MemberLevel.findByPoint(point)
            .orElseThrow(IllegalArgumentException::new);

        //then
        assertThat(memberLevel).isEqualTo(expectedLevel);
    }
}
