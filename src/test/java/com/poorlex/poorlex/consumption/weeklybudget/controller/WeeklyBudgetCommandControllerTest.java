package com.poorlex.poorlex.consumption.weeklybudget.controller;

import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetCommandService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.support.ControllerTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("주간 예산 관리 Controller 단위 테스트")
@WebMvcTest(
        controllers = WeeklyBudgetCommandController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}
)
class WeeklyBudgetCommandControllerTest extends ControllerTest implements ReplaceUnderScoreTest {

    @MockBean
    private WeeklyBudgetCommandService weeklyBudgetCommandService;

    @BeforeEach
    void setUp() {
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
    }

    @Test
    void 주간_예산을_생성한다() throws Exception {
        //given
        final WeeklyBudgetCreateRequest 주간_예산_생성_요청 = new WeeklyBudgetCreateRequest(10000L);

        //when
        //then
        mockMvc.perform(
                        post("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .content(objectMapper.writeValueAsString(주간_예산_생성_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void ERROR_주간_예산생성시_금액이_적절하지_않을_경우_상태코드로_응답한다() throws Exception {
        //given
        final long 부적절한_예산_금액 = -1L;
        STUBBING_주간_예산이_다음값일떄_예외를_발생한다(부적절한_예산_금액);
        final WeeklyBudgetCreateRequest 주간_예산_생성_요청 = new WeeklyBudgetCreateRequest(부적절한_예산_금액);

        //when
        //then
        mockMvc.perform(
                        post("/weekly-budgets")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .content(objectMapper.writeValueAsString(주간_예산_생성_요청))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private void STUBBING_주간_예산이_다음값일떄_예외를_발생한다(final Long amount) {
        willThrow(new ApiException(ExceptionTag.WEEKLY_BUDGET_AMOUNT, "errorMessage"))
                .given(weeklyBudgetCommandService)
                .createBudgetWithCurrentDuration(any(), eq(amount));
    }
}
