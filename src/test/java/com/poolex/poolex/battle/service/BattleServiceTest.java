package com.poolex.poolex.battle.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.fixture.BattleCreateRequestFixture;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("배틀 서비스 테스트")
class BattleServiceTest extends TestDataJpaTest implements ReplaceUnderScoreTest {

    private BattleService battleService;

    @Autowired
    private BattleRepository battleRepository;

    @BeforeEach
    void setUp() {
        battleService = new BattleService(battleRepository);
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
}
