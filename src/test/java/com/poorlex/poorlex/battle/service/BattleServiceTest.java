package com.poorlex.poorlex.battle.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmViewHistoryRepository;
import com.poorlex.poorlex.alarm.battlealarm.service.BattleAlarmService;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.domain.BattleDuration;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.domain.BattleType;
import com.poorlex.poorlex.battle.fixture.BattleCreateRequestFixture;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.poorlex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.battlealarmreaction.domain.AlarmReactionRepository;
import com.poorlex.poorlex.battlealarmreaction.service.AlarmReactionService;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.service.ExpenditureService;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.member.service.MemberService;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.service.MemberPointService;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.votingpaper.domain.VotingPaperRepository;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("배틀 서비스 테스트")
class BattleServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime BATTLE_START_DATE = LocalDateTime.of(
        LocalDate.of(2023, 12, 25),
        LocalTime.of(9, 0)
    );
    private static final LocalDateTime BATTLE_END_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 31),
        LocalTime.of(22, 0)
    );
    private static final BattleDuration BATTLE_DURATION = new BattleDuration(BATTLE_START_DATE, BATTLE_END_TIME);

    private BattleService battleService;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingPaperRepository votingPaperRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private AlarmReactionRepository alarmReactionRepository;

    @Autowired
    private BattleAlarmViewHistoryRepository battleAlarmViewHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @BeforeEach
    void setUp() {
        battleService = new BattleService(
            battleRepository,
            battleParticipantRepository,
            new BattleAlarmService(
                battleAlarmRepository,
                new VoteService(voteRepository, battleParticipantRepository),
                new VotingPaperService(voteRepository, votingPaperRepository, battleParticipantRepository),
                new AlarmReactionService(alarmReactionRepository, battleAlarmRepository),
                battleAlarmViewHistoryRepository
            ),
            new MemberPointService(memberPointRepository, memberRepository),
            new ExpenditureService(battleRepository, expenditureRepository),
            new MemberService(memberRepository)
        );
    }

    @Test
    void 배틀을_생성한다() {
        //given
        final long createMemberId = 1L;
        final BattleCreateRequest request = BattleCreateRequestFixture.simple();

        //when
        battleService.create(createMemberId, request);

        //then
        final List<Battle> battles = battleRepository.findAll();
        assertSoftly(
            (softly) -> {
                softly.assertThat(battles).hasSize(1);
                softly.assertThat(battles.get(0)).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(BattleFixture.simple());
            }
        );
    }

    @Test
    void 배틀을_생성한다_참여한_배틀이_3개일_경우_예외를_던진다() {
        //given
        final Member member = createMemberWithOauthId("oauthId");

        final Battle battle1 = battleRepository.save(BattleFixture.simple());
        final Battle battle2 = battleRepository.save(BattleFixture.simple());
        final Battle battle3 = battleRepository.save(BattleFixture.simple());

        join(member, battle1);
        join(member, battle2);
        join(member, battle3);

        final BattleCreateRequest request = BattleCreateRequestFixture.simple();

        //when
        //then
        assertThatThrownBy(() -> battleService.create(member.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "멤버1의 지출이 {0}, 멤버2의 지출이 {1} 일 때")
    @CsvSource(
        value = {"1000:1000:1", "1000:2000:1", "2000:1000:2"},
        delimiter = ':'
    )
    void 멤버가_포함된_진행중인_배틀들의_정보를_조회한다(final int member1Expenditure,
                                    final int member2Expenditure,
                                    final int expectedRank) {
        //given
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Battle battle = createSimpleBattleWithStatusAndDuration(10000, BattleStatus.PROGRESS, BATTLE_DURATION);

        join(member1, battle);
        join(member2, battle);

        expend(member1Expenditure, member1, BATTLE_START_DATE);
        expend(member2Expenditure, member2, BATTLE_START_DATE);

        //when
        final List<MemberProgressBattleResponse> battles =
            battleService.findProgressMemberBattles(member1.getId(), LocalDate.from(BATTLE_START_DATE));

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(battles).hasSize(1);
                softly.assertThat(battles.get(0).getDDay()).isEqualTo(6);
                softly.assertThat(battles.get(0).getBudgetLeft()).isEqualTo(10000 - member1Expenditure);
                softly.assertThat(battles.get(0).getCurrentParticipantRank()).isEqualTo(expectedRank);
            }
        );
    }

    @ParameterizedTest(name = "멤버1의 지출이 {0}, 멤버2의 지출이 {1} 일 때")
    @CsvSource(
        value = {"1000:1000:1", "1000:2000:1", "2000:1000:2"},
        delimiter = ':'
    )
    void 멤버가_포함되어있는_종료된_배틀들의_정보를_조회한다(final int member1Expenditure,
                                      final int member2Expenditure,
                                      final int expectedRank) {
        //given
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Battle battle = createSimpleBattleWithStatusAndDuration(10000, BattleStatus.COMPLETE, BATTLE_DURATION);

        join(member1, battle);
        join(member2, battle);

        expend(member1Expenditure, member1, BATTLE_START_DATE);
        expend(member2Expenditure, member2, BATTLE_START_DATE);

        //when
        final List<MemberCompleteBattleResponse> battles =
            battleService.findCompleteMemberBattles(member1.getId(), LocalDate.from(BATTLE_END_TIME).plusDays(1));

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(battles).hasSize(1);
                softly.assertThat(battles.get(0).getPastDay()).isEqualTo(1);
                softly.assertThat(battles.get(0).getCurrentParticipantRank()).isEqualTo(expectedRank);
                softly.assertThat(battles.get(0).getBudgetLeft()).isEqualTo(10000 - member1Expenditure);
                softly.assertThat(battles.get(0).getEarnedPoint()).isEqualTo(BattleType.LARGE.getScore(expectedRank));
            }
        );
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Member join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }

    private Battle createSimpleBattleWithStatusAndDuration(final int budget,
                                                           final BattleStatus status,
                                                           final BattleDuration battleDuration) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .budget(new BattleBudget(budget))
            .status(status)
            .duration(battleDuration)
            .build();

        return battleRepository.save(battle);
    }

    private void expend(final int amount, final Member member, final LocalDateTime date) {
        expenditureRepository.save(ExpenditureFixture.simpleWith(amount, member.getId(), date));
    }
}
