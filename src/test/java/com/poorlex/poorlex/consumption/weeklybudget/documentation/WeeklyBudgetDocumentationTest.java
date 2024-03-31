package com.poorlex.poorlex.consumption.weeklybudget.documentation;

import com.poorlex.poorlex.consumption.weeklybudget.controller.WeeklyBudgetController;
import com.poorlex.poorlex.consumption.weeklybudget.service.WeeklyBudgetService;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetLeftRequest;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.request.WeeklyBudgetRequest;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poorlex.poorlex.consumption.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(WeeklyBudgetController.class)
class WeeklyBudgetDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeeklyBudgetService weeklyBudgetService;

    @Test
    void create() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(10000L);

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(weeklyBudgetService).createBudget(anyLong(), anyLong());

        //when
        final ResultActions result = mockMvc.perform(
                post("/weekly-budgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(
                        document("weekly-budget-create",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("budget").type(JsonFieldType.NUMBER).description("주간 예산 금액")
                                 )
                        ));
    }

    @Test
    void find_member_weekly_budget() throws Exception {
        //given
        final WeeklyBudgetRequest request = new WeeklyBudgetRequest(LocalDate.now());

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(weeklyBudgetService.findCurrentBudgetByMemberIdAndDate(any(), any())).willReturn(
                new WeeklyBudgetResponse(true, 10000L, 5L)
        );

        //when
        final ResultActions result = mockMvc.perform(
                get("/weekly-budgets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("weekly-budget-find",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("date").type(JsonFieldType.STRING)
                                                 .description("조회 일자")
                                 ),
                                 responseFields(
                                         fieldWithPath("exist").type(JsonFieldType.BOOLEAN)
                                                 .description("요청 시간이 포함된 주의 주간 예산 등록 여부"),
                                         fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                                 .description("요청 시간이 포함된 주의 주간 예산"),
                                         fieldWithPath("dday").type(JsonFieldType.NUMBER)
                                                 .description("요청 시간이 포함된 주의 종료까지의 D-Day")
                                 )
                        ));
    }

    @Test
    void find_member_left_weekly_budget() throws Exception {
        //given
        final WeeklyBudgetLeftRequest request = new WeeklyBudgetLeftRequest(LocalDate.now());

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(weeklyBudgetService.findCurrentBudgetLeftByMemberIdAndDate(any(), any())).willReturn(
                new WeeklyBudgetLeftResponse(true, 10000L)
        );

        //when
        final ResultActions result = mockMvc.perform(
                get("/weekly-budgets/left")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("weekly-budget-left-find",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("date").type(JsonFieldType.STRING)
                                                 .description("조회 일자")
                                 ),
                                 responseFields(
                                         fieldWithPath("exist").type(JsonFieldType.BOOLEAN)
                                                 .description("요청 시간이 포함된 주의 주간 예산 등록 여부"),
                                         fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                                 .description("요청 시간이 포함된 주의 남은 주간 예산")
                                 )
                        ));
    }
}
