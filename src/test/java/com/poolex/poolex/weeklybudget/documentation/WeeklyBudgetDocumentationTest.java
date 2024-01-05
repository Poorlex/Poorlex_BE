package com.poolex.poolex.weeklybudget.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poolex.poolex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poolex.poolex.config.auth.argumentresolver.MemberInfo;
import com.poolex.poolex.config.auth.interceptor.TokenInterceptor;
import com.poolex.poolex.weeklybudget.controller.WeeklyBudgetController;
import com.poolex.poolex.weeklybudget.service.WeeklyBudgetService;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetCreateRequest;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetLeftRequest;
import com.poolex.poolex.weeklybudget.service.dto.request.WeeklyBudgetRequest;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetLeftResponse;
import com.poolex.poolex.weeklybudget.service.dto.response.WeeklyBudgetResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WeeklyBudgetController.class)
@AutoConfigureRestDocs
class WeeklyBudgetDocumentationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private TokenInterceptor tokenInterceptor;

    @MockBean
    private MemberArgumentResolver memberArgumentResolver;

    @MockBean
    private WeeklyBudgetService weeklyBudgetService;

    @Test
    void create() throws Exception {
        //given
        final WeeklyBudgetCreateRequest request = new WeeklyBudgetCreateRequest(10000);
        given(tokenInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new MemberInfo(1L));
        doNothing().when(weeklyBudgetService).createBudget(anyLong(), anyInt());

        //when
        final ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.post("/weekly-budgets")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                document("weekly-budget-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("budget").type(JsonFieldType.NUMBER).description("주간 예산 금액")
                    )
                ));
    }

    @Test
    void find_member_weekly_budget() throws Exception {
        //given
        final WeeklyBudgetRequest request = new WeeklyBudgetRequest(LocalDateTime.now());

        given(tokenInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new MemberInfo(1L));
        given(weeklyBudgetService.findCurrentBudgetByMemberIdAndDate(any(), any())).willReturn(
            new WeeklyBudgetResponse(true, 10000)
        );

        //when
        final ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.get("/weekly-budgets")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document("weekly-budget-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING)
                            .description("조회하려는 시간 [ yyyy-mm-ddThh:mm ]")
                    ),
                    responseFields(
                        fieldWithPath("exist").type(JsonFieldType.BOOLEAN).description("요청 시간이 포함된 주의 주간 예산 등록 여부"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("요청 시간이 포함된 주의 주간 예산")
                    )
                ));
    }

    @Test
    void find_member_left_weekly_budget() throws Exception {
        //given
        final WeeklyBudgetLeftRequest request = new WeeklyBudgetLeftRequest(LocalDateTime.now());

        given(tokenInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new MemberInfo(1L));
        given(weeklyBudgetService.findCurrentBudgetLeftByMemberIdAndDate(any(), any())).willReturn(
            new WeeklyBudgetLeftResponse(true, 10000)
        );

        //when
        final ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.get("/weekly-budgets/left")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document("weekly-budget-left-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING)
                            .description("조회하려는 시간 [ yyyy-mm-ddThh:mm ]")
                    ),
                    responseFields(
                        fieldWithPath("exist").type(JsonFieldType.BOOLEAN).description("요청 시간이 포함된 주의 주간 예산 등록 여부"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("요청 시간이 포함된 주의 남은 주간 예산")
                    )
                ));
    }
}
