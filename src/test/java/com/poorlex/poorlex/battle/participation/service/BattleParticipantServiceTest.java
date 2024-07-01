package com.poorlex.poorlex.battle.participation.service;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.BadRequestException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("배틀 참가 서비스 테스트")
class BattleParticipantServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private BattleParticipantService battleParticipantService;

    @BeforeEach
    void setUp() {
        battleParticipantService = new BattleParticipantService(
                memberRepository,
                battleRepository,
                battleParticipantRepository,
                weeklyBudgetRepository
        );
        initializeDataBase();
    }

    @Test
    void 배틀참가자를_생성한다() {
        //given
        final Long memberId = createMember().getId();
        createWeeklyBudget(memberId);
        final Battle recruitingBattle = createBattleWithStatus(BattleStatus.RECRUITING);

        //when
        //then
        assertDoesNotThrow(() -> battleParticipantService.participate(recruitingBattle.getId(), memberId));
    }

    @Test
    void 배틀참가자가_참가하려는_배틀이_없을_경우_예외를_던진다() {
        //given
        final Long memberId = createMember().getId();
        createWeeklyBudget(memberId);
        final long notExistBattleId = -1L;

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.participate(notExistBattleId, memberId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 예산을_설정하지_않고_배틀에_참여하려는_경우_예외() {
        //given
        final Long memberId = createMember().getId();
        final Battle battle = createBattleWithStatus(BattleStatus.RECRUITING);

        //when
        //then
        final String expectedErrorMessage = "예산을 먼저 설정해야만 배틀에 참여할 수 있습니다.";
        assertThatThrownBy(() -> battleParticipantService.participate(battle.getId(), memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.WEEKLY_BUDGET_STATUS)
                .hasMessage(expectedErrorMessage);;
    }

    @Test
    void 배틀참가자가_이미_3개의_배틀이_참가되어있을_경우_예외를_던진다() {
        //given
        final Member member = createMember();
        createWeeklyBudget(member.getId());

        final Battle battle1 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle2 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle3 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle4 = createBattleWithStatus(BattleStatus.RECRUITING);

        join(member, battle1);
        join(member, battle2);
        join(member, battle3);

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.participate(battle4.getId(), member.getId()))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void 참가한_배틀이_완료된_경우_참가_개수_제한에_영향을_주지_않는다() {
        //given
        final Member member = createMember();
        createWeeklyBudget(member.getId());

        final Battle battle1 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle2 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle3 = createBattleWithStatus(BattleStatus.RECRUITING);
        final Battle battle4 = createBattleWithStatus(BattleStatus.RECRUITING);

        join(member, battle1);
        join(member, battle2);
        join(member, battle3);

        //when
        battle1.endWithoutValidate();

        //then
        assertDoesNotThrow(() -> battleParticipantService.participate(battle4.getId(), member.getId()));
    }

    @ParameterizedTest(name = "배틀의 상태가 {0}일 때")
    @CsvSource(value = {"RECRUITING_FINISHED", "PROGRESS", "COMPLETE"})
    void 배틀참가자가_참가하려는_배틀이_모집중이_아닐_경우_예외를_던진다(final BattleStatus invalidBattleStatus) {
        //given
        final Long memberId = createMember().getId();
        createWeeklyBudget(memberId);
        final Battle battleWithStatus = createBattleWithStatus(invalidBattleStatus);
        final Long battleId = battleWithStatus.getId();

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.participate(battleId, memberId))
                .isInstanceOf(ApiException.class);
    }

    @ParameterizedTest(name = "배틀의 상태가 {0} 일 때")
    @CsvSource(value = {"RECRUITING", "RECRUITING_FINISHED"})
    void 배틀이_시작전_배틀_참자가를_제거한다(final BattleStatus battleStatus) {
        //given
        final Member member = createMember();
        final Battle battle = createBattleWithStatus(battleStatus);
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));

        //when
        battleParticipantService.withdraw(battle.getId(), member.getId());

        //then
        final Optional<BattleParticipant> removedBattleParticipant = battleParticipantRepository.findByBattleIdAndMemberId(
                battle.getId(),
                member.getId()
        );
        assertThat(removedBattleParticipant).isEmpty();
    }

    @ParameterizedTest(name = "배틀의 상태가 {0} 일 때")
    @CsvSource(value = {"COMPLETE"})
    void 배틀이_종료된_후_배틀_참자가를_제거시_예외를_던진다(final BattleStatus battleStatus) {
        //given
        final Member member = createMember();
        final Battle battle = createBattleWithStatus(battleStatus);
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.withdraw(battle.getId(), member.getId()))
                .isInstanceOf(ApiException.class);

    }

    @ParameterizedTest(name = "배틀의 상태가 {0} 일 때")
    @CsvSource(value = {"RECRUITING", "RECRUITING_FINISHED"})
    void 배틀의_매니저가_참가를_취소할시_예외를_던진다(final BattleStatus battleStatus) {
        //given
        final Member member = createMember();
        final Battle battle = createBattleWithStatus(battleStatus);
        battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.withdraw(battle.getId(), member.getId()))
                .isInstanceOf(ApiException.class);
    }

    private Member createMember() {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
    }

    private void createWeeklyBudget(Long memberId) {
        WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(new WeeklyBudgetAmount(100000L), memberId);
        weeklyBudgetRepository.save(weeklyBudget);
    }

    private Battle createBattleWithStatus(final BattleStatus status) {
        final Battle battle = BattleFixture.initialBattleBuilder()
                .status(status)
                .build();
        return battleRepository.save(battle);
    }

    private Member join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);

        return member;
    }
}
