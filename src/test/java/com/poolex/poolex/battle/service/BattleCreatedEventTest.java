package com.poolex.poolex.battle.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.battle.service.event.BattleCreatedEvent;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@DisplayName("배틀 생성 이벤트를 테스트한다.")
@RecordApplicationEvents
class BattleCreatedEventTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleService battleService;

    @Autowired
    private ApplicationEvents events;

    @Test
    void 배틀을_생성한다() {
        //given
        final long createMemberId = 1L;
        final BattleCreateRequest request = BattleCreateRequestFixture.simple();

        //when
        battleService.create(createMemberId, request);

        //then
        assertThat(events.stream(BattleCreatedEvent.class).count()).isOne();
    }
}
