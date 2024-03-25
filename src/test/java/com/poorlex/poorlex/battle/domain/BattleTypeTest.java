package com.poorlex.poorlex.battle.domain;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("배틀 사이즈 타입 테스트")
class BattleTypeTest implements ReplaceUnderScoreTest {

    @ParameterizedTest(name = "인원이 {0} 일 경우 배틀의 타입은 {1} 이다. ")
    @CsvSource(value = {
            "1:SMALL", "2:SMALL", "3:SMALL", "4:SMALL", "5:SMALL",
            "6:LARGE", "7:LARGE", "8:LARGE", "9:LARGE", "10:LARGE"
    }, delimiter = ':')
    void 인원에_따라_배틀의_타입이_결정된다(final int participantSize, final BattleType expectedType) {
        //given
        final BattleParticipantSize battleParticipantSize = new BattleParticipantSize(participantSize);

        //when
        final Optional<BattleType> byParticipantSize = BattleType.findByParticipantSize(battleParticipantSize);

        //then
        assertThat(byParticipantSize).isPresent()
                .contains(expectedType);
    }
}
