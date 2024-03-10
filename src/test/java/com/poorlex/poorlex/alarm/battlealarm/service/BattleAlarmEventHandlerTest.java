package com.poorlex.poorlex.alarm.battlealarm.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battlenotification.service.BattleNotificationService;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.event.BattleNotificationChangedEvent;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class BattleAlarmEventHandlerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private BattleAlarmEventHandler battleAlarmEventHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleNotificationService battleNotificationService;

    @Test
    void 공지가_생성되면_공지_변경_이벤트를_처리한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        joinAsManager(battle, member);

        //when
        battleNotificationService.createNotification(
            battle.getId(),
            member.getId(),
            new BattleNotificationCreateRequest("notificationContentNotificationContent", "imageUrl")
        );

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                softly.assertThat(battleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = battleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }


    @Test
    void 공지_변경_이벤트를_처리한다() {
        //given
        final BattleNotificationChangedEvent event = new BattleNotificationChangedEvent(1L, 1L);

        //when
        battleAlarmEventHandler.notificationChangedAlarm(event);

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                softly.assertThat(battleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = battleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(1L);
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(1L);
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(
                        LocalDateTime.now().truncatedTo(ChronoUnit.MICROS).truncatedTo(ChronoUnit.MICROS));
            }
        );
    }


    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.simple());
    }

    private void joinAsManager(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));
    }
}
