package com.poorlex.poorlex.alarm.battlealarm.service;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistory;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistoryRepository;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.AbstractBattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReaction;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionContent;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionRepository;
import com.poorlex.poorlex.battlealarmreaction.service.AlarmReactionService;
import com.poorlex.poorlex.battlealarmreaction.service.dto.response.AlarmReactionResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.voting.vote.domain.Vote;
import com.poorlex.poorlex.voting.vote.domain.VoteAmount;
import com.poorlex.poorlex.voting.vote.domain.VoteDuration;
import com.poorlex.poorlex.voting.vote.domain.VoteDurationType;
import com.poorlex.poorlex.voting.vote.domain.VoteName;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.domain.VoteStatus;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperRepository;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BattleAlarmServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private AlarmReactionRepository alarmReactionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingPaperRepository votingPaperRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private BattleAlarmService battleAlarmService;

    @BeforeEach
    void setUp() {
        this.battleAlarmService = new BattleAlarmService(
                battleAlarmRepository,
                new VoteService(voteRepository, battleParticipantRepository),
                new VotingPaperService(voteRepository, votingPaperRepository, battleParticipantRepository),
                new AlarmReactionService(alarmReactionRepository, battleAlarmRepository),
                battleAlarmViewHistoryRepository);
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_없을_때() {
        //given
        final long battleId = 1L;
        final long memberId = 1L;
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));

        //when
        final List<AbstractBattleAlarmResponse> battleAlarms = battleAlarmService.findBattleAlarms(battleId, memberId,
                                                                                                   request);

        //then
        assertThat(battleAlarms).isEmpty();
    }

    @Test
    void 배틀에_포함된_모든_알림을_조회한다_알림이_있을_때() {
        //given
        final long battleId = 1L;
        final Member member = createMemberWithOauthId("oauthId");
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        final BattleAlarm battleAlarm1 = createAlarm(battleId, member.getId(), BattleAlarmType.EXPENDITURE_CREATED);
        final BattleAlarm battleAlarm2 = createAlarm(battleId, member.getId(), BattleAlarmType.EXPENDITURE_NEEDED);
        final BattleAlarm battleAlarm3 = createAlarm(battleId, member.getId(), BattleAlarmType.OVER_BUDGET);
        final AlarmReaction alarmReaction = createAlarmReaction(battleAlarm1.getId(), member.getId());
        final Vote vote1 = createVote(battleId, member.getId());
        final Vote vote2 = createVote(battleId, member.getId());

        //when
        final List<AbstractBattleAlarmResponse> battleAlarms = battleAlarmService.findBattleAlarms(battleId,
                                                                                                   member.getId(),
                                                                                                   request);

        //then
        final List<AbstractBattleAlarmResponse> expectedResponse = new ArrayList<>(List.of(
                BattleAlarmResponse.from(battleAlarm1),
                BattleAlarmResponse.from(battleAlarm2),
                BattleAlarmResponse.from(battleAlarm3),
                AlarmReactionResponse.from(alarmReaction),
                new VoteResponse(
                        vote1.getId(),
                        member.getNickname(),
                        vote1.getName(), vote1.getStatus().name(), vote1.getAmount(), 0, 0, vote1.getStart()
                ),
                new VoteResponse(
                        vote2.getId(),
                        member.getNickname(),
                        vote2.getName(), vote2.getStatus().name(), vote2.getAmount(), 0, 0, vote2.getStart()
                )
        ));
        expectedResponse.sort(Comparator.comparing(AbstractBattleAlarmResponse::getCreatedAt));

        assertThat(battleAlarms).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void 배틀_참가자가_읽지_않은_알림의_개수를_반환한다_알림을_조회한적이_없을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthID");
        join(member, battle);
        createAlarm(battle.getId(), member.getId(), BattleAlarmType.EXPENDITURE_CREATED);

        //when
        final UncheckedBattleAlarmCountResponse response =
                battleAlarmService.getBattleParticipantUncheckedAlarmCount(battle.getId(), member.getId());

        //then
        assertThat(response.getCount()).isOne();
    }

    @Test
    void 배틀_참가자가_읽지_않은_알림의_개수를_반환한다_알림을_조회한적이_있을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMemberWithOauthId("oauthID");
        join(member, battle);
        createAlarm(battle.getId(), member.getId(), BattleAlarmType.EXPENDITURE_CREATED);
        viewAlarm(battle, member);
        createAlarm(battle.getId(), member.getId(), BattleAlarmType.EXPENDITURE_NEEDED);

        //when
        final UncheckedBattleAlarmCountResponse response =
                battleAlarmService.getBattleParticipantUncheckedAlarmCount(battle.getId(), member.getId());

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
                battleAlarmService.getBattleParticipantUncheckedAlarmCount(battle.getId(), member.getId());

        //then
        assertThat(response.getCount()).isZero();
    }

    private void viewAlarm(final Battle battle, final Member member) {
        battleAlarmViewHistoryRepository.save(
                BattleAlarmViewHistory.withoutId(
                        battle.getId(),
                        member.getId(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
                )
        );
    }

    private BattleAlarm createAlarm(final Long battleId, final Long memberId, final BattleAlarmType battleAlarmType) {
        return battleAlarmRepository.save(BattleAlarm.withoutId(battleId, memberId, battleAlarmType));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private AlarmReaction createAlarmReaction(final Long alarmId, final Long memberId) {
        return alarmReactionRepository.save(
                AlarmReaction.praiseWithoutId(alarmId, memberId, new AlarmReactionContent("칭찬"))
        );
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Vote createVote(final Long battleId, final Long memberId) {
        return voteRepository.save(
                Vote.withoutId(
                        battleId,
                        memberId,
                        new VoteAmount(1000),
                        new VoteDuration(LocalDateTime.now(), VoteDurationType.TEN_MINUTE),
                        new VoteName("초코우유"),
                        VoteStatus.PROGRESS
                )
        );
    }

    private Member join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }
}
