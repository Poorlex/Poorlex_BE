package com.poolex.poolex.alarm.battlealarm.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarm;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poolex.poolex.alarm.battlealarm.domain.BattleAlarmType;
import com.poolex.poolex.alarm.battlealarm.service.BattleAlarmEventHandler;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.battlenotification.domain.BattleNotification;
import com.poolex.poolex.battlenotification.domain.BattleNotificationContent;
import com.poolex.poolex.battlenotification.domain.BattleNotificationRepository;
import com.poolex.poolex.battlenotification.service.BattleNotificationService;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poolex.poolex.battlenotification.service.event.BattleNotificationChangedEvent;
import com.poolex.poolex.expenditure.service.ExpenditureService;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class BattleBattleAlarmEventHandlerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private ApplicationEvents events;

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
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private BattleNotificationService battleNotificationService;

    @Autowired
    private ExpenditureService expenditureService;

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
                final long eventListenCount = events.stream(BattleNotificationChangedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();
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
    void 공지가_변경되면_공지_변경_이벤트를_처리한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        joinAsManager(battle, member);
        createBattleNotification(battle);

        //when
        battleNotificationService.updateNotification(
            battle.getId(),
            member.getId(),
            new BattleNotificationUpdateRequest("notificationContentNotificationContent", "imageUrl")
        );

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(BattleNotificationChangedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();
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

    @Test
    void 지출이_등록되면_지출_생성_이벤트를_처리한다() {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsManager(battle, member);

        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            1000,
            "description",
            List.of("imageUrl"),
            battle.getDuration().getStart()
        );

        //when
        expenditureService.createExpenditure(member.getId(), request);

        //then
        final List<BattleAlarm> expenditureCreatedBattleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.EXPENDITURE_CREATED);
        final List<BattleAlarm> overBudgetBattleAlarms = battleAlarmRepository.findByType(BattleAlarmType.OVER_BUDGET);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(ExpenditureCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(expenditureCreatedBattleAlarms).hasSize(1);
                softly.assertThat(overBudgetBattleAlarms).isEmpty();

                final BattleAlarm battleAlarm = expenditureCreatedBattleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.EXPENDITURE_CREATED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 지출_생성_이벤트를_처리할_때_배틀의_예산보다_주간_지출이_클_경우_지출_초과_알림을_생성한다() {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsManager(battle, member);

        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            11000,
            "description",
            List.of("imageUrl"),
            battle.getDuration().getStart()
        );

        //when
        expenditureService.createExpenditure(member.getId(), request);

        //then
        final List<BattleAlarm> expenditureCreatedBattleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.EXPENDITURE_CREATED);
        final List<BattleAlarm> overBudgetBattleAlarms = battleAlarmRepository.findByType(BattleAlarmType.OVER_BUDGET);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(ExpenditureCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(expenditureCreatedBattleAlarms).hasSize(1);
                softly.assertThat(overBudgetBattleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = overBudgetBattleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.OVER_BUDGET);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.simple());
    }

    private Battle createBattle(final BattleStatus battleStatus) {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(battleStatus).build());
    }

    private void joinAsManager(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));
    }

    private void createBattleNotification(final Battle battle) {
        battleNotificationRepository.save(BattleNotification.withoutIdAndImageUrl(
            battle.getId(),
            new BattleNotificationContent("ThisIsBattleNotificationContent"))
        );
    }
}
