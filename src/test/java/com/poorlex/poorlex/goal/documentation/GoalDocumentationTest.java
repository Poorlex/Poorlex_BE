package com.poorlex.poorlex.goal.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.goal.controller.GoalController;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.GoalService;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalTypeResponse;
import com.poorlex.poorlex.support.RestDocsDocumentationTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(GoalController.class)
class GoalDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @Test
    void create_goal() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(goalService.createGoal(any(), any())).willReturn(1L);

        final GoalCreateRequest request = new GoalCreateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/goals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isCreated())
            .andDo(
                document("goal-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description("목표 타입"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("목표 명"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("목표 금액"),
                        fieldWithPath("startDate").type(JsonFieldType.STRING).description("목표 시작 날짜"),
                        fieldWithPath("endDate").type(JsonFieldType.STRING).description("목표 종료 날짜")
                    )
                )
            );
    }

    @Test
    void goal_find_ids() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(goalService.findMemberGoalIds(any()))
            .willReturn(List.of(
                new GoalIdResponse(1L),
                new GoalIdResponse(2L),
                new GoalIdResponse(3L))
            );

        //when
        //then
        final ResultActions result = mockMvc.perform(
            get("/goals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        result.andExpect(status().isOk())
            .andDo(
                document("goal-find-ids",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    responseFields(fieldWithPath("[]").description("목표 리스트"))
                        .andWithPrefix("[].",
                            fieldWithPath("goalId").type(JsonFieldType.NUMBER).description("회원이 등록한 목표 Id")
                        )
                )
            );
    }

    @Test
    void goal_find_types() throws Exception {
        //given
        given(goalService.findAllGoalType())
            .willReturn(
                Arrays.stream(GoalType.values())
                    .map(GoalTypeResponse::from)
                    .toList()
            );

        //when
        //then
        final ResultActions result = mockMvc.perform(get("/goals/types"));

        result.andExpect(status().isOk())
            .andDo(
                document("goal-find-types",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    responseFields()
                        .andWithPrefix("[].",
                            fieldWithPath("recommendGoalNames[]").description("목표 타입별 추천 목표명"),
                            fieldWithPath("typeName").type(JsonFieldType.STRING).description("목표 타입명")
                        )
                )
            );
    }
}
