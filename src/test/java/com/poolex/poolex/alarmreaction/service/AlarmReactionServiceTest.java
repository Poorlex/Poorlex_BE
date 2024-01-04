package com.poolex.poolex.alarmreaction.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.AlarmType;
import com.poolex.poolex.alarmreaction.domain.AlarmReactionRepository;
import com.poolex.poolex.alarmreaction.service.dto.request.AlarmReactionCreateRequest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class AlarmReactionServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private AlarmReactionRepository alarmReactionRepository;

    private AlarmReactionService alarmReactionService;

    @BeforeEach
    void setUp() {
        this.alarmReactionService = new AlarmReactionService(alarmReactionRepository, alarmRepository);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"BATTLE_NOTIFICATION_CHANGED", "EXPENDITURE_NEEDED"})
    void 알림_타입이_공지변경이거나_지출입력인_경우_알림반응을_생성할_수_없다(final AlarmType alarmType) {
        //given
        final long memberId = 1L;
        final Alarm alarm = alarmRepository.save(Alarm.withoutId(1L, memberId, alarmType));
        final AlarmReactionCreateRequest request = new AlarmReactionCreateRequest(alarm.getId(), "PRAISE", "알림 반응 문구");

        //when
        //then
        assertThatThrownBy(() -> alarmReactionService.createAlarmReaction(memberId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "알림 타입이 {0} 인 경우")
    @CsvSource(value = {"EXPENDITURE_CREATED", "OVER_BUDGET", "ZERO_EXPENDITURE"})
    void 알림반응을_생성할_수_있는_알림의_반응을_생성한다(final AlarmType alarmType) {
        //given
        final long memberId = 1L;
        final Alarm alarm = alarmRepository.save(Alarm.withoutId(1L, memberId, alarmType));
        final AlarmReactionCreateRequest request = new AlarmReactionCreateRequest(alarm.getId(), "PRAISE", "알림 반응 문구");

        //when
        //then
        assertDoesNotThrow(() -> alarmReactionService.createAlarmReaction(memberId, request));
    }
}
