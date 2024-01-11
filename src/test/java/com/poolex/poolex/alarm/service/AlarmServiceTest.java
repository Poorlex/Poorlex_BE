package com.poolex.poolex.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.alarm.domain.Alarm;
import com.poolex.poolex.alarm.domain.AlarmRepository;
import com.poolex.poolex.alarm.domain.AlarmType;
import com.poolex.poolex.alarm.domain.BattleAlarmViewHistoryRepository;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlarmServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private AlarmService alarmService;

    @BeforeEach
    void setUp() {
        this.alarmService = new AlarmService(alarmRepository, battleAlarmViewHistoryRepository);
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_없을_때() {
        //given
        final long battleId = 1L;
        final long memberId = 1L;
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now());

        //when
        final List<BattleAlarmResponse> battleAlarms = alarmService.findBattleAlarms(battleId, memberId, request);

        //then
        assertThat(battleAlarms).isEmpty();
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_있을_때() {
        //given
        final long battleId = 1L;
        final long memberId = 1L;
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now());
        final Alarm alarm1 = createAlarm(battleId, memberId, AlarmType.EXPENDITURE_CREATED);
        final Alarm alarm2 = createAlarm(battleId, memberId, AlarmType.EXPENDITURE_NEEDED);
        final Alarm alarm3 = createAlarm(battleId, memberId, AlarmType.OVER_BUDGET);

        //when
        final List<BattleAlarmResponse> battleAlarms = alarmService.findBattleAlarms(battleId, memberId, request);

        //then
        final List<BattleAlarmResponse> expectedReponse = List.of(
            BattleAlarmResponse.from(alarm1),
            BattleAlarmResponse.from(alarm2),
            BattleAlarmResponse.from(alarm3)
        );
        assertThat(battleAlarms).hasSize(3);
        assertThat(battleAlarms).usingRecursiveComparison().isEqualTo(expectedReponse);
    }

    @Test
    void 배틀_참가자가_읽지_않은_알림의_개수를_반환한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthID");
        join(member, battle);
        createAlarm(battle.getId(), member.getId(), AlarmType.EXPENDITURE_CREATED);

        //when
        final UncheckedBattleAlarmCountResponse response =
            alarmService.getBattleParticipantUncheckedAlarmCount(battle.getId(), member.getId());

        //then
        assertThat(response.getCount()).isOne();
    }

    @Test
    void 배틀_참가자가_읽지_않은_알림의_개수를_반환한다_알림이_없을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthID");
        join(member, battle);

        //when
        final UncheckedBattleAlarmCountResponse response =
            alarmService.getBattleParticipantUncheckedAlarmCount(battle.getId(), member.getId());

        //then
        assertThat(response.getCount()).isZero();
    }

    private Alarm createAlarm(final Long battleId, final Long memberId, final AlarmType alarmType) {
        return alarmRepository.save(Alarm.withoutId(battleId, memberId, alarmType));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Member join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }
}
