package com.poorlex.poorlex.battle.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleParticipantSize;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.participate.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class BattleParticipantChangedEventHandlerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleParticipantChangedEventHandler battleParticipantChangedEventHandler;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void 배틀에_참가자가_추가되었을때_배틀의_최대인원과_같아진경우_배틀의_상태를_모집완료로_변경한다() {
        //given
        final Battle battle = createBattleWithMaxSize(1);
        final Member member = createMember("oauthId");
        join(member, battle);

        //when
        battleParticipantChangedEventHandler.added(new BattleParticipantAddedEvent(battle.getId()));

        //then
        final BattleStatus battleStatus = battleRepository.findById(battle.getId())
                .orElseThrow(IllegalArgumentException::new)
                .getStatus();

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

    private void join(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }
}
