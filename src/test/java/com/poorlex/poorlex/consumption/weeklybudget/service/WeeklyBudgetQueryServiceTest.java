package com.poorlex.poorlex.consumption.weeklybudget.service;

import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudget;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetAmount;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetDuration;
import com.poorlex.poorlex.consumption.weeklybudget.domain.WeeklyBudgetRepository;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.provider.TotalExpenditureProvider;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class WeeklyBudgetQueryServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private WeeklyBudgetRepository weeklyBudgetRepository;

    @Mock
    private TotalExpenditureProvider totalExpenditureProvider;

    private WeeklyBudgetQueryService weeklyBudgetQueryService;

    @BeforeEach
    void setUp() {
        this.weeklyBudgetQueryService =
                new WeeklyBudgetQueryService(weeklyBudgetRepository, memberId -> true, totalExpenditureProvider);
        initializeDataBase();
    }

    @Test
    void 멤버의_현재날짜_기준_주간_예산을_조회한다() {
        //given
        final Long 회원_ID = 1L;
        final Long 주간_예산_금액 = 1000L;
        final WeeklyBudget 주간_예산 = 주간_예산을_생성한다(회원_ID, 주간_예산_금액, LocalDate.now());
        final LocalDate 주간_예산_기간_시작일 = 주간_예산.getDuration().getStart();

        //when
        final WeeklyBudgetResponse 주간_예산_응답 = weeklyBudgetQueryService.findWeeklyBudgetByMemberIdAndDate(
                회원_ID,
                주간_예산_기간_시작일);

        //then
        assertThat(주간_예산_응답.isExist()).isTrue();
        assertThat(주간_예산_응답.getAmount()).isEqualTo(주간_예산_금액);
    }

    @Test
    void 멤버의_현재날짜_기준_주간_예산을_조회한다_등록한_주간_예산이_없을_때() {
        //given
        final Long 회원_ID = 1L;

        //when
        final WeeklyBudgetResponse 주간_예산_응답 = weeklyBudgetQueryService.findWeeklyBudgetByMemberIdAndDate(회원_ID,
                                                                                                         LocalDate.now());

        //then
        assertThat(주간_예산_응답.isExist()).isFalse();
        assertThat(주간_예산_응답.getAmount()).isZero();
    }

    @Test
    void 멤버의_현재날짜_기준_주간_예산에서_지출을_뺀_금액을_조회한다() {
        //given
        final Long 주간_예산_기간내_지출_금액 = 100L;
        STUBBING_지정한_금액으로_주간_예산_기간내_지출금액을_반환하도록한다(주간_예산_기간내_지출_금액);

        final Long 회원_ID = 1L;
        final Long 주간_예산_금액 = 1000L;
        final WeeklyBudget 주간_예산 = 주간_예산을_생성한다(회원_ID, 주간_예산_금액, LocalDate.now());
        final LocalDate 주간_예산_기간_시작일 = 주간_예산.getDuration().getStart();

        //when
        final WeeklyBudgetLeftResponse 주간_에산에서_지출을_뺀_금액_응답 = weeklyBudgetQueryService.findWeeklyBudgetLeftByMemberIdAndDate(
                회원_ID,
                주간_예산_기간_시작일);

        //then
        assertThat(주간_에산에서_지출을_뺀_금액_응답.isExist()).isTrue();
        assertThat(주간_에산에서_지출을_뺀_금액_응답.getAmount()).isEqualTo(주간_예산_금액 - 주간_예산_기간내_지출_금액);
    }

    @Test
    void 멤버의_현재날짜_기준_주간_예산에서_지출을_뺀_금액을_조회한다_등록한_주간_예산이_없을_때() {
        //given
        final Long 회원_ID = 1L;

        //when
        final WeeklyBudgetLeftResponse 주간_에산에서_지출을_뺀_금액_응답 = weeklyBudgetQueryService.findWeeklyBudgetLeftByMemberIdAndDate(
                회원_ID,
                LocalDate.now());

        //then
        assertThat(주간_에산에서_지출을_뺀_금액_응답.isExist()).isFalse();
        assertThat(주간_에산에서_지출을_뺀_금액_응답.getAmount()).isZero();
    }

    private void STUBBING_지정한_금액으로_주간_예산_기간내_지출금액을_반환하도록한다(final Long 주간_예산_기간내_지출_금액) {
        when(totalExpenditureProvider.byMemberIdBetween(any(), any(), any())).thenReturn(주간_예산_기간내_지출_금액);
    }

    private WeeklyBudget 주간_예산을_생성한다(final Long memberId, final Long amount, final LocalDate date) {
        final WeeklyBudget weeklyBudget = WeeklyBudget.withoutId(
                new WeeklyBudgetAmount(amount),
                WeeklyBudgetDuration.from(date),
                memberId
        );

        return weeklyBudgetRepository.save(weeklyBudget);
    }
}
