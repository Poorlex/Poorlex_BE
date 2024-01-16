package com.poolex.poolex.battle.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleParticipantSize;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.service.BattleParticipantService;
import com.poolex.poolex.participate.service.event.BattleParticipantAddedEvent;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class BattleParticipantChangedEventHandlerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private BattleParticipantService battleParticipantService;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 배틀에_참가자가_추가되었을때_배틀의_최대인원과_같아진경우_배틀의_상태를_모집완료로_변경한다() {
        //given
        final Battle battle = createBattleWithMaxSize(1);
        final Member member = createMember("oauthId");

        //when
        battleParticipantService.create(battle.getId(), member.getId());

        //then
        final BattleStatus battleStatus = battleRepository.findById(battle.getId())
            .orElseThrow(IllegalArgumentException::new)
            .getStatus();
        final long eventCount = events.stream(BattleParticipantAddedEvent.class).count();

        assertThat(eventCount).isOne();
        assertThat(battleStatus).isEqualTo(BattleStatus.RECRUITING_FINISHED);
    }

    public Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    public Battle createBattleWithMaxSize(final int count) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .battleParticipantSize(new BattleParticipantSize(count))
            .build();
        return battleRepository.save(battle);
    }
}
