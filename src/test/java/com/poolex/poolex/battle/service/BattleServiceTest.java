package com.poolex.poolex.battle.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.battle.service.event.BattleCreatedEvent;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@ActiveProfiles("test")
@SpringBootTest
@RecordApplicationEvents
class BattleServiceTest {

    @Autowired
    private BattleService battleService;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private ApplicationEvents events;

    @DisplayName("유효한 입력값들을 통한 배틀 생성에 성공한다.")
    @Test
    void create_success() {
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
                softly.assertThat(events.stream(BattleCreatedEvent.class).count()).isOne();
            }
        );
    }
}
