package com.poorlex.poorlex.consumption.weeklybudget.controller;

import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetQueryService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import com.poorlex.poorlex.support.ControllerTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.BDDMockito.when;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("주간 예산 조회 Controller 단위 테스트")
@WebMvcTest(WeeklyBudgetQueryController.class)
class WeeklyBudgetQueryControllerTest extends ControllerTest implements ReplaceUnderScoreTest {

    @MockBean
    private WeeklyBudgetQueryService weeklyBudgetQueryService;

    @BeforeEach
    void setUp() {
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
    }

    @Test
    void 주간_예산을_조회한다_존재할_때() throws Exception {
        //given
        final boolean 주간_예산_존재_여부 = true;
        final long 주간_예산_금액 = 10000L;
        final long 주간_예산_기간_종료까지_남은_일수 = 6L;

        STUBBING_주간_예산_조회시_해당응답을_반환하도록_한다(주간_예산_존재_여부, 주간_예산_금액, 주간_예산_기간_종료까지_남은_일수);

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(주간_예산_존재_여부))
                .andExpect(jsonPath("$.amount").value(주간_예산_금액))
                .andExpect(jsonPath("$.daysBeforeEnd").value(주간_예산_기간_종료까지_남은_일수));
    }

    @Test
    void 주간_예산을_조회한다_존재하지_않을_때() throws Exception {
        //given
        final boolean 주간_예산_존재_여부 = false;
        final long 주간_예산_금액 = 0L;
        final long 주간_예산_기간_종료까지_남은_일수 = 0L;

        STUBBING_주간_예산_조회시_해당응답을_반환하도록_한다(주간_예산_존재_여부, 주간_예산_금액, 주간_예산_기간_종료까지_남은_일수);

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(주간_예산_존재_여부))
                .andExpect(jsonPath("$.amount").value(주간_예산_금액))
                .andExpect(jsonPath("$.daysBeforeEnd").value(주간_예산_기간_종료까지_남은_일수));
    }

    @Test
    void 남은_주간_예산을_조회한다() throws Exception {
        //given
        final boolean 주간_예산_존재_여부 = true;
        final long 주간_예산에서_지출을_뺀_금액 = 9000L;
        STUBBING_주간_예산에서_지출을뺀_나머지_조회시_해당응답을_반환하도록_한다(주간_예산_존재_여부, 주간_예산에서_지출을_뺀_금액);

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets/left")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(주간_예산_존재_여부))
                .andExpect(jsonPath("$.amount").value(주간_예산에서_지출을_뺀_금액));
    }

    @Test
    void 남은_주간_예산을_조회한다_등록된_주간_예산이_없을때() throws Exception {
        //given
        final boolean 주간_예산_존재_여부 = false;
        final long 주간_예산에서_지출을_뺀_금액 = 0L;
        STUBBING_주간_예산에서_지출을뺀_나머지_조회시_해당응답을_반환하도록_한다(주간_예산_존재_여부, 주간_예산에서_지출을_뺀_금액);

        //when
        //then
        mockMvc.perform(
                        get("/weekly-budgets/left")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exist").value(주간_예산_존재_여부))
                .andExpect(jsonPath("$.amount").value(주간_예산에서_지출을_뺀_금액));
    }

    private void STUBBING_주간_예산_조회시_해당응답을_반환하도록_한다(final boolean 주간_예산_존재_여부,
                                                   final long 주간_예산_금액,
                                                   final long 주간_예산_기간_종료까지_남은_일수) {
        when(weeklyBudgetQueryService.findCurrentWeeklyBudgetByMemberId(ArgumentMatchers.any()))
                .thenReturn(new WeeklyBudgetResponse(주간_예산_존재_여부, 주간_예산_금액, 주간_예산_기간_종료까지_남은_일수));
    }

    private void STUBBING_주간_예산에서_지출을뺀_나머지_조회시_해당응답을_반환하도록_한다(final boolean 주간_예산_존재_여부, final long 주간_예산_금액) {
        when(weeklyBudgetQueryService.findCurrentWeeklyBudgetLeftByMemberId(ArgumentMatchers.any()))
                .thenReturn(new WeeklyBudgetLeftResponse(주간_예산_존재_여부, 주간_예산_금액));
    }
}
