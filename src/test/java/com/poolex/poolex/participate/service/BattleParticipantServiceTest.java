package com.poolex.poolex.participate.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleParticipantSize;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
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
    private BattleParticipantRepository battleParticipantRepository;
    private BattleParticipantService battleParticipantService;

    @BeforeEach
    void setUp() {
        battleParticipantService = new BattleParticipantService(battleRepository, battleParticipantRepository);
    }

    @Test
    void 배틀참가자를_생성한다() {
        //given
        final Long memberId = createMember().getId();
        final Battle recruitingBattle = createBattleWithStatus(BattleStatus.RECRUITING);

        //when
        //then
        assertDoesNotThrow(() -> battleParticipantService.create(recruitingBattle.getId(), memberId));
    }

    @Test
    void 배틀참가자가_참가하려는_배틀이_없을_경우_예외를_던진다() {
        //given
        final Long memberId = createMember().getId();
        final long notExistBattleId = -1L;

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.create(notExistBattleId, memberId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 배틀참가자가_참가하려는_배틀이_꽉_찼을_때_예외를_던진다() {
        //given
        final Long memberId = createMember().getId();
        final Battle battle = createBattleWithParticipantSize(new BattleParticipantSize(1));
        final Long battleId = battle.getId();
        battleParticipantRepository.save(BattleParticipant.manager(battleId, memberId));

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.create(battleId, memberId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "배틀의 상태가 {0}일 때")
    @CsvSource(value = {"RECRUITING_FINISHED", "PROGRESS", "COMPLETE"})
    void 배틀참가자가_참가하려는_배틀이_모집중이_아닐_경우_예외를_던진다(final BattleStatus invalidBattleStatus) {
        //given
        final Long memberId = createMember().getId();
        final Battle battleWithStatus = createBattleWithStatus(invalidBattleStatus);
        final Long battleId = battleWithStatus.getId();

        //when
        //then
        assertThatThrownBy(() -> battleParticipantService.create(battleId, memberId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Member createMember() {
        return memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
    }

    private Battle createBattleWithStatus(final BattleStatus status) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .status(status)
            .build();
        return battleRepository.save(battle);
    }

    private Battle createBattleWithParticipantSize(final BattleParticipantSize participantSize) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .battleParticipantSize(participantSize)
            .build();

        return battleRepository.save(battle);
    }
}
