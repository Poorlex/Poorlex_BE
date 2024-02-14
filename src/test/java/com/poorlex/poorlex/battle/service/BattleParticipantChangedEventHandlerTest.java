package com.poorlex.poorlex.battle.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleParticipantSize;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.service.BattleParticipantService;
import com.poorlex.poorlex.participate.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.SpringEventTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BattleParticipantChangedEventHandlerTest extends SpringEventTest implements ReplaceUnderScoreTest {

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
        battleParticipantService.participate(battle.getId(), member.getId());

        //then
        final BattleStatus battleStatus = battleRepository.findById(battle.getId())
            .orElseThrow(IllegalArgumentException::new)
            .getStatus();
        final long eventCount = events.stream(BattleParticipantAddedEvent.class).count();

        assertThat(eventCount).isOne();
        assertThat(battleStatus).isEqualTo(BattleStatus.RECRUITING_FINISHED);
    }

    public Member createMember(final String oauthId) {
        return memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname")));
    }

    public Battle createBattleWithMaxSize(final int count) {
        final Battle battle = BattleFixture.initialBattleBuilder()
            .battleParticipantSize(new BattleParticipantSize(count))
            .build();
        return battleRepository.save(battle);
    }
}
