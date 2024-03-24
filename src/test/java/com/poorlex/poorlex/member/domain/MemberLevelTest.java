package com.poorlex.poorlex.member.domain;

import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest(name = "레벨이 {0}일 때")
    @CsvSource(value = {
            "LEVEL_4:LEVEL_5",
            "LEVEL_3:LEVEL_4",
            "LEVEL_2:LEVEL_3",
            "LEVEL_1:LEVEL_2"
    }, delimiter = ':')
    void 레벨의_구간의_길이를_반환한다(final MemberLevel currentLevel, final MemberLevel nextLevel) {
        //given
        //when
        final int levelRange = currentLevel.getLevelRange();

        //then
        final int expectedRange = nextLevel.getLowerBound() - currentLevel.getLowerBound();
        assertThat(levelRange).isEqualTo(expectedRange);
    }
}
