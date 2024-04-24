package com.poorlex.poorlex.batch.scheduler;

import com.poorlex.poorlex.battle.battle.domain.BattleDuration;
import com.poorlex.poorlex.battle.battle.domain.BattleParticipantWithExpenditure;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.succession.domain.BattleSuccessHistoryRepository;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class BattleBatchSchedulerTest extends UsingDataJpaTest {

    private static final Long NUMBER_OF_BATTLE_DAYS = Long.valueOf(BattleDuration.BATTLE_DAYS);

    @MockBean
    private BattleRepository battleRepository;

    @MockBean
    private MemberPointRepository memberPointRepository;

    @Autowired
    private BattleSuccessHistoryRepository battleSuccessHistoryRepository;

    private BattleBatchScheduler battleBatchScheduler;

    @BeforeEach
    void setUp() {
        this.battleBatchScheduler = new BattleBatchScheduler(battleRepository,
                                                             memberPointRepository,
                                                             battleSuccessHistoryRepository);
    }


    @Test
    void 배틀참가자가_배틀기간에_모든_지출을_등록시_포인트를_지급한다() {
        //given
        final List<BattleParticipantWithExpenditure> 배틀_참가자_지출_정보_목록 = List.of(
                new FakeBattleParticipantWithExpenditure(1L, NUMBER_OF_BATTLE_DAYS),
                new FakeBattleParticipantWithExpenditure(2L, NUMBER_OF_BATTLE_DAYS)
        );

        MOCKING_진행중인_배틀을_탐색시_임의의_배틀을_반환한다();
        MOCKING_배틀참가자_지출_정보_목록을_조회시_다음_목록을_반환한다(배틀_참가자_지출_정보_목록);

        //when
        battleBatchScheduler.giveBattlePointToParticipants();

        //then
        verify(memberPointRepository, times(배틀_참가자_지출_정보_목록.size())).save(any());
    }

    private void MOCKING_배틀참가자_지출_정보_목록을_조회시_다음_목록을_반환한다(final List<BattleParticipantWithExpenditure> 배틀_참가자_지출_정보_목록) {
        when(battleRepository.findBattleParticipantsWithExpenditureByBattleId(any()))
                .thenReturn(배틀_참가자_지출_정보_목록);
    }

    private void MOCKING_진행중인_배틀을_탐색시_임의의_배틀을_반환한다() {
        when(battleRepository.findBattlesByStatusIn(List.of(BattleStatus.PROGRESS)))
                .thenReturn(List.of(BattleFixture.simple()));
    }

    @Test
    void 배틀기간에_모든_지출을_등록하지_않은_참가자에게는_포인트를_지급하지_않는다() {
        //given
        final List<BattleParticipantWithExpenditure> 배틀_참가자_지출_정보_목록 = List.of(
                new FakeBattleParticipantWithExpenditure(1L, 7L),
                new FakeBattleParticipantWithExpenditure(2L, 6L)
        );

        MOCKING_진행중인_배틀을_탐색시_임의의_배틀을_반환한다();
        MOCKING_배틀참가자_지출_정보_목록을_조회시_다음_목록을_반환한다(배틀_참가자_지출_정보_목록);

        //when
        battleBatchScheduler.giveBattlePointToParticipants();

        //then
        verify(memberPointRepository, times(1)).save(any());
    }

    private static class FakeBattleParticipantWithExpenditure implements BattleParticipantWithExpenditure {

        private final Long memberId;
        private final Long expenditureCount;

        public FakeBattleParticipantWithExpenditure(final Long memberId, final Long expenditureCount) {
            this.memberId = memberId;
            this.expenditureCount = expenditureCount;
        }

        @Override
        public BattleParticipant getBattleParticipant() {
            return BattleParticipant.manager(1L, memberId);
        }

        @Override
        public Long getExpenditure() {
            return 10000L;
        }

        @Override
        public Long getExpenditureCount() {
            return expenditureCount;
        }
    }
}
