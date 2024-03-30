package com.poorlex.poorlex.alarm.memberalram.service;

import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowance;
import com.poorlex.poorlex.alarm.alarmallowance.domain.AlarmAllowanceRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberAlarmServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AlarmAllowanceRepository alarmAllowanceRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private MemberAlarmService memberAlarmService;

    @BeforeEach
    void setUp() {
        this.memberAlarmService = new MemberAlarmService(
                memberAlarmRepository,
                alarmAllowanceRepository,
                new MemberAlarmResponseConverter(memberRepository, battleRepository, battleParticipantRepository)
        );
    }

    @Test
    void 멤버의_알림목록을_조회한다() {
        //given
        final Battle battle = createBattle();
        final Member me = createMemberWithOauthId("oauthId2");
        final Member other = createMemberWithOauthId("oauthId1");
        final BattleParticipant battleParticipant = join(battle, other);
        createMemberAlarmAllowance(me.getId());


        final MemberAlarm battleInvitationAlarm = memberAlarmRepository.save(MemberAlarm.withoutId(
                me.getId(),
                battleParticipant.getId(),
                MemberAlarmType.BATTLE_INVITATION_NOT_ACCEPTED)
        );
        final MemberAlarm friendInvitation = memberAlarmRepository.save(MemberAlarm.withoutId(
                me.getId(),
                other.getId(),
                MemberAlarmType.FRIEND_INVITATION_NOT_ACCEPTED)
        );
        final LocalDateTime requestDateTime = LocalDateTime.now().plusMinutes(10);

        //when
        final List<MemberAlarmResponse> responses = memberAlarmService.findMemberAlarms(me.getId(), requestDateTime);

        //then
        assertThat(responses).hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorOnFields()
                .containsExactly(
                        MemberAlarmResponse.from(battleInvitationAlarm,
                                                 other.getNickname(),
                                                 battle.getName(),
                                                 requestDateTime),
                        MemberAlarmResponse.from(friendInvitation, other.getNickname(), null, requestDateTime)
                );
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder()
                                             .status(BattleStatus.RECRUITING).build());
    }

    private BattleParticipant join(final Battle battle, final Member member) {
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }

    private void createMemberAlarmAllowance(final Long memberId) {
        alarmAllowanceRepository.save(AlarmAllowance.withoutIdWithAllAllowed(memberId));
    }
}
