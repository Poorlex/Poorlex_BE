package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.support.ControllerTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("배틀 지출 조회 Controller 단위 테스트")
@WebMvcTest(
        controllers = BattleExpenditureQueryController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class}
)
class BattleExpenditureQueryControllerTest extends ControllerTest implements ReplaceUnderScoreTest {

    @MockBean
    private ExpenditureQueryService expenditureQueryService;

    @BeforeEach
    void setUp() {
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
    }

    @Test
    void 멤버의_배틀기간_모든_지출_목록을_조회한다() throws Exception {
        //given
        final Long 배틀_ID = 1L;
        STUBBING_회원_배틀기간_지출_목록을_조회시_두개의_지출을_반환한다();

        //when
        //then
        mockMvc.perform(
                        get("/battles/{battleId}/expenditures", 배틀_ID)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void 배틀참가자들의_요일별_지출_목록을_조회한다() throws Exception {
        //given
        final Long 배틀_ID = 1L;
        STUBBING_배틀기간중_요일에_해당하는_지출_목록을_조회시_두개의_지출을_반환한다();

        //when
        //then
        mockMvc.perform(
                        get("/battles/{battleId}/expenditures", 배틀_ID)
                                .queryParam("dayOfWeek", "MONDAY")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    private void STUBBING_회원_배틀기간_지출_목록을_조회시_두개의_지출을_반환한다() {
        final BattleExpenditureResponse 첫번째_지출_응답 = 배틀_지출_응답을_생성한다(1L, "메인이미지_URL");
        final BattleExpenditureResponse 두번째_지출_응답 = 배틀_지출_응답을_생성한다(1L, "메인이미지_URL");

        when(expenditureQueryService.findMemberBattleExpenditures(any(), any()))
                .thenReturn(List.of(첫번째_지출_응답, 두번째_지출_응답));
    }

    private void STUBBING_배틀기간중_요일에_해당하는_지출_목록을_조회시_두개의_지출을_반환한다() {
        final BattleExpenditureResponse 첫번째_지출_응답 = 배틀_지출_응답을_생성한다(1L, "메인이미지_URL");
        final BattleExpenditureResponse 두번째_지출_응답 = 배틀_지출_응답을_생성한다(1L, "메인이미지_URL");

        when(expenditureQueryService.findBattleExpendituresInDayOfWeek(any(), any(), any()))
                .thenReturn(List.of(첫번째_지출_응답, 두번째_지출_응답));
    }

    private BattleExpenditureResponse 배틀_지출_응답을_생성한다(final Long 지출_ID, final String 지출_메인이미지_URL) {
        return new BattleExpenditureResponse(지출_ID, 지출_메인이미지_URL, 1, true);
    }
}
