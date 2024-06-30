package com.poorlex.poorlex.consumption.expenditure.controller;

import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.support.ControllerTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("지출 조회 Controller 단위 테스트")
@WebMvcTest(ExpenditureQueryController.class)
class ExpenditureQueryControllerTest extends ControllerTest implements ReplaceUnderScoreTest {

    @MockBean
    private ExpenditureQueryService expenditureQueryService;

    @BeforeEach
    void setUp() {
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
    }

    @Test
    void 멤버의_모든_지출_목록을_조회한다() throws Exception {
        //given
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
        STUBBING_회원_지출_목록을_조회시_두개의_지출을_반환한다();

        //when
        //then
        mockMvc.perform(
                        get("/expenditures")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void Id에_해당하는_지출을_조회한다() throws Exception {
        //given
        final long 지출_ID = 1L;
        final long 지출_생성_멤버_ID = 1L;
        final LocalDate 지출_생성_날짜 = LocalDate.now();
        final long 지출_금액 = 1000L;
        final String 지출_설명 = "설명";
        final String 지출_메인이미지_URL = "메인이미지URL";
        final String 지출_서브이미지_URL = "서브이미지URL";

        STUBBING_지출ID를_통해_지출조회시_다음값들을_가진_지출을_반환한다(지출_ID, 지출_생성_멤버_ID, 지출_생성_날짜, 지출_금액, 지출_설명, 지출_메인이미지_URL, 지출_서브이미지_URL);

        //when
        //then
        mockMvc.perform(get("/expenditures/{expenditureId}", 지출_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(지출_ID))
                .andExpect(jsonPath("$.date").value(지출_생성_날짜.toString()))
                .andExpect(jsonPath("$.amount").value(지출_금액))
                .andExpect(jsonPath("$.description").value(지출_설명))
                .andExpect(jsonPath("$.mainImageUrl").value(지출_메인이미지_URL))
                .andExpect(jsonPath("$.subImageUrl").value(지출_서브이미지_URL));
    }

    @Test
    void 멤버의_주간_지출의_총합을_조회한다() throws Exception {
        //given
        final Long 주간_지출_총합 = 3000L;
        STUBBING_토큰에서_해당_회원ID를_추출하도록한다(1L);
        STUBBING_회원의_주간_지출_조회시_다음값을_반환한다(주간_지출_총합);

        //when
        //then
        mockMvc.perform(
                        get("/expenditures/weekly")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(주간_지출_총합));
    }

    private OngoingStubbing<ExpenditureResponse> STUBBING_지출ID를_통해_지출조회시_다음값들을_가진_지출을_반환한다(final long 지출_ID,
                                                                                           final long 지출_생성_멤버_ID,
                                                                                           final LocalDate 지출_생성_날짜,
                                                                                           final long 지출_금액,
                                                                                           final String 지출_설명,
                                                                                           final String 지출_메인이미지_URL,
                                                                                           final String 지출_서브이미지_URL) {
        return when(expenditureQueryService.findExpenditureById(any())).thenReturn(new ExpenditureResponse(지출_ID,
                                                                                                           지출_생성_멤버_ID,
                                                                                                           지출_생성_날짜,
                                                                                                           지출_금액,
                                                                                                           지출_설명,
                                                                                                           지출_메인이미지_URL,
                                                                                                           지출_서브이미지_URL));
    }

    private void STUBBING_회원_지출_목록을_조회시_두개의_지출을_반환한다() {
        final ExpenditureResponse 첫번째_지출_응답 = 지출_응답을_생성한다(1L, 1L, LocalDate.now(), 1000L, "설명", "메인이미지", "서브이미지");
        final ExpenditureResponse 두번째_지출_응답 = 지출_응답을_생성한다(1L, 1L, LocalDate.now(), 1000L, "설명", "메인이미지", "서브이미지");

        when(expenditureQueryService.findMemberExpenditures(any(), any()))
                .thenReturn(List.of(첫번째_지출_응답, 두번째_지출_응답));
    }

    private ExpenditureResponse 지출_응답을_생성한다(final Long 지출_ID,
                                            final long 지출_생성_멤버_ID,
                                            final LocalDate 지출_생성_날짜,
                                            final Long 지출_금액,
                                            final String 지출_설명,
                                            final String 지출_메인이미지_URL,
                                            final String 지출_서브이미지_URL) {
        return new ExpenditureResponse(지출_ID, 지출_생성_멤버_ID, 지출_생성_날짜, 지출_금액, 지출_설명, 지출_메인이미지_URL, 지출_서브이미지_URL);
    }

    private void STUBBING_회원의_주간_지출_조회시_다음값을_반환한다(final Long amount) {
        when(expenditureQueryService.findMemberCurrentWeeklyTotalExpenditure(any()))
                .thenReturn(new MemberWeeklyTotalExpenditureResponse(amount));
    }
}
