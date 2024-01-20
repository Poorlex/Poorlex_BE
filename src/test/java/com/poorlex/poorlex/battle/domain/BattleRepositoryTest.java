package com.poorlex.poorlex.battle.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BattleRepositoryTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime BATTLE_START_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 25),
        LocalTime.of(9, 0)
    );
    private static final LocalDateTime BATTLE_END_TIME = LocalDateTime.of(
        LocalDate.of(2023, 12, 31),
        LocalTime.of(22, 0)
    );
    private static final BattleDuration BATTLE_DURATION = new BattleDuration(BATTLE_START_TIME, BATTLE_END_TIME);

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Test
    void 사용자_참여중인_배틀목록을_조회한다_2개_이상일_때() {
        //given
        final Battle progressBattle1 = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Battle progressBattle2 = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Member member = createMember("oauthId");

        join(progressBattle1, member);
        join(progressBattle2, member);

        expend(1000, member, BATTLE_START_TIME);
        expend(2000, member, BATTLE_START_TIME);

        //when
        final List<BattleWithMemberExpenditure> progressBattleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(member.getId(), BattleStatus.PROGRESS);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(progressBattleInfos).hasSize(2);

                final BattleWithMemberExpenditure battleInfo1 = progressBattleInfos.get(0);
                softly.assertThat(battleInfo1.getBattle()).isEqualTo(progressBattle1);
                softly.assertThat(battleInfo1.getExpenditure()).isEqualTo(3000);

                final BattleWithMemberExpenditure battleInfo2 = progressBattleInfos.get(1);
                softly.assertThat(battleInfo2.getExpenditure()).isEqualTo(3000);
            }
        );
    }

    @Test
    void 사용자_참여중인_배틀목록을_조회한다_2개_이상이고_지출이_없을_떄() {
        //given
        final Battle progressBattle1 = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Battle progressBattle2 = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Member member = createMember("oauthId");

        join(progressBattle1, member);
        join(progressBattle2, member);

        //when
        final List<BattleWithMemberExpenditure> progressBattleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(member.getId(), BattleStatus.PROGRESS);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(progressBattleInfos).hasSize(2);

                final BattleWithMemberExpenditure battleInfo1 = progressBattleInfos.get(0);
                softly.assertThat(battleInfo1.getBattle()).isEqualTo(progressBattle1);
                softly.assertThat(battleInfo1.getExpenditure()).isEqualTo(0);

                final BattleWithMemberExpenditure battleInfo2 = progressBattleInfos.get(1);
                softly.assertThat(battleInfo2.getExpenditure()).isEqualTo(0);
            }
        );
    }

    @Test
    void 사용자_참여중인_배틀목록을_조회한다_1개일_때() {
        //given
        final Battle progressBattle = createBattle(BattleStatus.PROGRESS, BattleRepositoryTest.BATTLE_DURATION);
        final Battle recruitingBattle = createBattle(BattleStatus.RECRUITING, BattleRepositoryTest.BATTLE_DURATION);
        final Member member = createMember("oauthId");

        join(progressBattle, member);
        join(recruitingBattle, member);

        expend(1000, member, BATTLE_START_TIME);
        expend(2000, member, BATTLE_START_TIME);

        //when
        final List<BattleWithMemberExpenditure> progressBattleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(member.getId(), BattleStatus.PROGRESS);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(progressBattleInfos).hasSize(1);

                final BattleWithMemberExpenditure battleInfo = progressBattleInfos.get(0);
                softly.assertThat(battleInfo.getBattle()).isEqualTo(progressBattle);
                softly.assertThat(battleInfo.getExpenditure()).isEqualTo(3000);
            }
        );
    }

    @Test
    void 사용자의_완료된_배틀목록을_조회한다_2개_이상일_때() {
        //given
        final Battle completeBattle1 = createBattle(BattleStatus.COMPLETE, BATTLE_DURATION);
        final Battle completeBattle2 = createBattle(BattleStatus.COMPLETE, BATTLE_DURATION);
        final Member member = createMember("oauthId");

        join(completeBattle1, member);
        join(completeBattle2, member);

        expend(1000, member, BATTLE_START_TIME);
        expend(2000, member, BATTLE_START_TIME);

        //when
        final List<BattleWithMemberExpenditure> completeBattleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(member.getId(), BattleStatus.COMPLETE);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(completeBattleInfos).hasSize(2);

                final BattleWithMemberExpenditure battleInfo1 = completeBattleInfos.get(0);
                softly.assertThat(battleInfo1.getBattle()).isEqualTo(completeBattle1);
                softly.assertThat(battleInfo1.getExpenditure()).isEqualTo(3000);

                final BattleWithMemberExpenditure battleInfo2 = completeBattleInfos.get(1);
                softly.assertThat(battleInfo2.getBattle()).isEqualTo(completeBattle2);
                softly.assertThat(battleInfo2.getExpenditure()).isEqualTo(3000);
            }
        );
    }

    @Test
    void 사용자의_완료된_배틀목록을_조회한다_1개일_때() {
        //given
        final Battle completeBattle = createBattle(BattleStatus.COMPLETE, BATTLE_DURATION);
        final Battle progressBattle = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Member member = createMember("oauthId");

        join(completeBattle, member);
        join(progressBattle, member);

        expend(1000, member, BATTLE_START_TIME);
        expend(2000, member, BATTLE_START_TIME);

        //when
        final List<BattleWithMemberExpenditure> completeBattleInfos =
            battleRepository.findMemberBattlesByMemberIdAndStatusWithExpenditure(member.getId(), BattleStatus.COMPLETE);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(completeBattleInfos).hasSize(1);

                final BattleWithMemberExpenditure battleInfo = completeBattleInfos.get(0);
                softly.assertThat(battleInfo.getBattle()).isEqualTo(completeBattle);
                softly.assertThat(battleInfo.getExpenditure()).isEqualTo(3000);
            }
        );
    }

    @Test
    void 해당_id를_가진_배틀의_참여자들의_지출_목록을_조회한다() {
        //given
        final Battle battle = createBattle(BattleStatus.PROGRESS, BATTLE_DURATION);
        final Member member1 = createMember("oauthId1");
        final Member member2 = createMember("oauthId2");

        final BattleParticipant battleParticipantMember1 = join(battle, member1);
        final BattleParticipant battleParticipantMember2 = join(battle, member2);
        expend(1000, member1, BATTLE_START_TIME);
        expend(2000, member1, BATTLE_START_TIME);
        expend(4000, member2, BATTLE_START_TIME);

        //when
        final List<BattleParticipantWithExpenditure> battleParticipantsWithExpenditure =
            battleRepository.findBattleParticipantsWithExpenditureByBattleId(battle.getId());

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(battleParticipantsWithExpenditure).hasSize(2);

                final BattleParticipantWithExpenditure participantWithExpenditure1 =
                    battleParticipantsWithExpenditure.get(0);
                softly.assertThat(participantWithExpenditure1.getBattleParticipant())
                    .isEqualTo(battleParticipantMember1);
                softly.assertThat(participantWithExpenditure1.getExpenditure()).isEqualTo(3000);

                final BattleParticipantWithExpenditure participantWithExpenditure2 =
                    battleParticipantsWithExpenditure.get(1);
                softly.assertThat(participantWithExpenditure2.getBattleParticipant())
                    .isEqualTo(battleParticipantMember2);
                softly.assertThat(participantWithExpenditure2.getExpenditure()).isEqualTo(4000);
            }
        );
    }

    private Battle createBattle(final BattleStatus status, final BattleDuration duration) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .status(status)
            .duration(duration)
            .build();
        return battleRepository.save(battle);
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private BattleParticipant join(final Battle battle, final Member member) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        return battleParticipantRepository.save(battleParticipant);
    }

    private Expenditure expend(final int amount, final Member member, final LocalDateTime date) {
        final Expenditure expenditure = ExpenditureFixture.simpleWith(amount, member.getId(), date);
        return expenditureRepository.save(expenditure);
    }
}
