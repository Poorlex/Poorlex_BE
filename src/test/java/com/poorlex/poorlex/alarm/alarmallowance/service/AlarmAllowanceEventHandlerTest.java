package com.poorlex.poorlex.alarm.alarmallowance.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.member.service.event.MemberRegisteredEvent;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlarmAllowanceEventHandlerTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private AlarmAllowanceRepository alarmAllowanceRepository;

    private AlarmAllowanceEventHandler alarmAllowanceEventHandler;

    @BeforeEach
    void setUp() {
        this.alarmAllowanceEventHandler = new AlarmAllowanceEventHandler(alarmAllowanceRepository);
    }

    @Test
    void 멤버_등록시_멤버의_알림허용_목록을_생성한다() {
        //given
        final Long memberId = 1L;
        final MemberRegisteredEvent memberRegisteredEvent = new MemberRegisteredEvent(memberId);

        //when
        alarmAllowanceEventHandler.handle(memberRegisteredEvent);

        //then
        final AlarmAllowance alarmAllowance = alarmAllowanceRepository.findByMemberId(memberId)
                .orElseThrow();
        assertThat(alarmAllowance).isNotNull();
        assertThat(alarmAllowance.getMemberId()).isEqualTo(memberId);
        assertThat(alarmAllowance.isAllowFriendAlarm()).isTrue();
        assertThat(alarmAllowance.isAllowExpenditureRequestAlarm()).isTrue();
        assertThat(alarmAllowance.isAllowBattleStatusAlarm()).isTrue();
        assertThat(alarmAllowance.isAllowBattleInvitationAlarm()).isTrue();
        assertThat(alarmAllowance.isAllowBattleChatAlarm()).isTrue();
    }
}