package com.poorlex.poorlex.user.member.domain;

import com.poorlex.poorlex.point.domain.Point;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("회원 레벨 테스트")
class MemberLevelTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "포인트가 {0}일 때")
    @CsvSource(value = {
            "1440:LEVEL_5",
            "1439:LEVEL_4",
            "599:LEVEL_3",
            "189:LEVEL_2",
            "69:LEVEL_1",
    }, delimiter = ':')
    void 포인트에_따른_레벨을_찾는다(final int 포인트양, final MemberLevel 예상_레벨) {
        //given
        final Point 포인트 = new Point(포인트양);

        //when
        final MemberLevel 회원_레벨 = MemberLevel.findByPoint(포인트)
                .orElseThrow(IllegalArgumentException::new);

        //then
        assertThat(회원_레벨).isEqualTo(예상_레벨);
    }

    @ParameterizedTest(name = "레벨이 {0}일 때")
    @CsvSource(value = {
            "LEVEL_4:LEVEL_5",
            "LEVEL_3:LEVEL_4",
            "LEVEL_2:LEVEL_3",
            "LEVEL_1:LEVEL_2"
    }, delimiter = ':')
    void 레벨의_구간의_길이를_반환한다(final MemberLevel 현재_레벨, final MemberLevel 다음_레벨) {
        //given
        //when
        final int 레벨간_포인트_차이 = 현재_레벨.getLevelRange();

        //then
        final int 예상_차이 = 다음_레벨.getLowerBound() - 현재_레벨.getLowerBound();
        assertThat(레벨간_포인트_차이).isEqualTo(예상_차이);
    }
}
