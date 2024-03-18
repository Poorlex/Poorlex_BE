package com.poorlex.poorlex.alarm.alarmallowance.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceType;
import com.poorlex.poorlex.alarm.alarmallowance.service.dto.request.AlarmAllowanceStatusChangeRequest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

class AlarmAllowanceCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private AlarmAllowanceRepository alarmAllowanceRepository;

    @Autowired
    private EntityManager entityManager;

    private AlarmAllowanceCommandService alarmAllowanceCommandService;

    @BeforeEach
    void setUp() {
        alarmAllowanceCommandService = new AlarmAllowanceCommandService(alarmAllowanceRepository);
    }

    @TestFactory
    Stream<DynamicTest> 알람_허용_내용을_수정한다() {
        //given
        final Long memberId = 1L;
        alarmAllowanceRepository.save(AlarmAllowance.withoutIdWithAllAllowed(memberId));
        final List<AlarmAllowanceTestInfo> alarmAllowanceTestInfos = List.of(
                new AlarmAllowanceTestInfo(AlarmAllowanceType.EXPENDITURE_REQUEST, false, true, true, true, true),
                new AlarmAllowanceTestInfo(AlarmAllowanceType.BATTLE_STATUS, false, false, true, true, true),
                new AlarmAllowanceTestInfo(AlarmAllowanceType.BATTLE_CHAT, false, false, false, true, true),
                new AlarmAllowanceTestInfo(AlarmAllowanceType.FRIEND, false, false, false, false, true),
                new AlarmAllowanceTestInfo(AlarmAllowanceType.BATTLE_INVITE, false, false, false, false, false)
        );

        return alarmAllowanceTestInfos.stream()
                .map(testInfo ->
                             dynamicTest(String.format("%s 타입의 알림을 차단한다", testInfo.blockAllowanceType.name()),
                                         () -> {
                                             //given
                                             final AlarmAllowanceStatusChangeRequest request =
                                                     new AlarmAllowanceStatusChangeRequest(testInfo.blockAllowanceType.name(),
                                                                                           false);

                                             //when
                                             alarmAllowanceCommandService.changeAlarmAllowanceStatus(memberId, request);
                                             entityManager.flush();
                                             entityManager.clear();

                                             //then
                                             final AlarmAllowance alarmAllowance =
                                                     alarmAllowanceRepository.findByMemberId(memberId).orElseThrow();

                                             assertSoftly(
                                                     softly -> {
                                                         softly.assertThat(alarmAllowance.isAllowFriendAlarm())
                                                                 .isEqualTo(testInfo.expectedFriendAlarmAllowance);
                                                         softly.assertThat(alarmAllowance.isAllowBattleChatAlarm())
                                                                 .isEqualTo(testInfo.expectedBattleChatAlarmAllowance);
                                                         softly.assertThat(alarmAllowance.isAllowBattleStatusAlarm())
                                                                 .isEqualTo(testInfo.expectedBattleStatusAlarmAllowance);
                                                         softly.assertThat(alarmAllowance.isAllowBattleInvitationAlarm())
                                                                 .isEqualTo(testInfo.expectedBattleInvitationAlarmAllowance);
                                                         softly.assertThat(alarmAllowance.isAllowExpenditureRequestAlarm())
                                                                 .isEqualTo(testInfo.expectedExpenditureRequestAlarmAllowance);
                                                     }
                                             );
                                         })
                );
    }

    private static class AlarmAllowanceTestInfo {

        final AlarmAllowanceType blockAllowanceType;
        final boolean expectedExpenditureRequestAlarmAllowance;
        final boolean expectedBattleStatusAlarmAllowance;
        final boolean expectedBattleChatAlarmAllowance;
        final boolean expectedFriendAlarmAllowance;
        final boolean expectedBattleInvitationAlarmAllowance;

        public AlarmAllowanceTestInfo(final AlarmAllowanceType blockAllowanceType,
                                      final boolean expectedExpenditureRequestAlarmAllowance,
                                      final boolean expectedBattleStatusAlarmAllowance,
                                      final boolean expectedBattleChatAlarmAllowance,
                                      final boolean expectedFriendAlarmAllowance,
                                      final boolean expectedBattleInvitationAlarmAllowance) {
            this.blockAllowanceType = blockAllowanceType;
            this.expectedExpenditureRequestAlarmAllowance = expectedExpenditureRequestAlarmAllowance;
            this.expectedBattleStatusAlarmAllowance = expectedBattleStatusAlarmAllowance;
            this.expectedBattleChatAlarmAllowance = expectedBattleChatAlarmAllowance;
            this.expectedFriendAlarmAllowance = expectedFriendAlarmAllowance;
            this.expectedBattleInvitationAlarmAllowance = expectedBattleInvitationAlarmAllowance;
        }
    }
}