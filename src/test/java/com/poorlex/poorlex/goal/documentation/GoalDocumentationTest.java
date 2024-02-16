package com.poorlex.poorlex.goal.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.goal.controller.GoalController;
import com.poorlex.poorlex.goal.domain.GoalDurationType;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.GoalService;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.request.GoalUpdateRequest;
import com.poorlex.poorlex.goal.service.dto.request.MemberGoalRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalTypeResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
class GoalDocumentationTest extends MockMvcTest {

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
                .with(csrf())
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
    void delete_goal() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(goalService).deleteGoal(any(), any());

        //when
        //then
        final ResultActions result = mockMvc.perform(
            delete("/goals/{goalId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .with(csrf())
        );

        result.andExpect(status().isNoContent())
            .andDo(
                document("goal-delete",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse()
                )
            );
    }

    @Test
    void update_goal() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(goalService).updateGoal(any(), any(), any());

        final GoalUpdateRequest request = new GoalUpdateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "수정할 목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        //then
        final ResultActions result = mockMvc.perform(
            patch("/goals/{goalId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        result.andExpect(status().isOk())
            .andDo(
                document("goal-update",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("type").type(JsonFieldType.STRING).description("수정할 목표 타입"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("수정할 목표 명"),
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("수정할 목표 금액"),
                        fieldWithPath("startDate").type(JsonFieldType.STRING).description("수정할 목표 시작 날짜"),
                        fieldWithPath("endDate").type(JsonFieldType.STRING).description("수정할 목표 종료 날짜")
                    )
                )
            );
    }

    @Test
    void find_progress_member_goals() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final MemberGoalRequest request = new MemberGoalRequest("PROGRESS", LocalDate.of(2023, 1, 1));
        given(goalService.findMemberGoalWithStatus(any(), any()))
            .willReturn(
                List.of(
                    new GoalResponse(
                        1L,
                        "목표명1",
                        GoalDurationType.MIDDLE.name(),
                        100000000,
                        ChronoUnit.DAYS.between(request.getDate(), LocalDate.of(2025, 1, 1)),
                        ChronoUnit.MONTHS.between(request.getDate(), LocalDate.of(2025, 1, 1)),
                        LocalDate.of(2022, 12, 20),
                        LocalDate.of(2025, 1, 1)
                    ),
                    new GoalResponse(
                        2L,
                        "목표명1",
                        GoalDurationType.SHORT.name(),
                        1000000,
                        ChronoUnit.DAYS.between(request.getDate(), LocalDate.of(2023, 1, 30)),
                        ChronoUnit.MONTHS.between(request.getDate(), LocalDate.of(2023, 1, 30)),
                        LocalDate.of(2022, 12, 1),
                        LocalDate.of(2023, 1, 30)
                    )
                )
            );

        //when
        //then
        final ResultActions result = mockMvc.perform(
            get("/goals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
            .andDo(
                document("goal-find-progress",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태 값: \"PROGRESS\""),
                        fieldWithPath("date").type(JsonFieldType.STRING).description("요청 날짜")
                    ),
                    responseFields(fieldWithPath("[]").description("진행중인 목표 리스트"))
                        .andWithPrefix("[].",
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("목표 ID"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("목표 명"),
                            fieldWithPath("durationType").type(JsonFieldType.STRING).description("목표 기간 타입"),
                            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("목표 금액"),
                            fieldWithPath("dayLeft").type(JsonFieldType.NUMBER).description("종료까지 남은 총 일수"),
                            fieldWithPath("monthLeft").type(JsonFieldType.NUMBER).description("종료까지 남은 총 개월 수"),
                            fieldWithPath("startDate").type(JsonFieldType.STRING).description("목표 시작 날짜"),
                            fieldWithPath("endDate").type(JsonFieldType.STRING).description("목표 종료 날짜")
                        )
                )
            );
    }

    @Test
    void find_finish_member_goals() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final MemberGoalRequest request = new MemberGoalRequest("FINISH", LocalDate.of(2023, 1, 1));
        given(goalService.findMemberGoalWithStatus(any(), any()))
            .willReturn(
                List.of(
                    new GoalResponse(
                        1L,
                        "목표명1",
                        GoalDurationType.MIDDLE.name(),
                        100000000,
                        0,
                        0,
                        LocalDate.of(2022, 12, 20),
                        LocalDate.of(2025, 1, 1)
                    ),
                    new GoalResponse(
                        2L,
                        "목표명1",
                        GoalDurationType.SHORT.name(),
                        1000000,
                        0,
                        0,
                        LocalDate.of(2022, 12, 1),
                        LocalDate.of(2023, 1, 30)
                    )
                )
            );

        //when
        //then
        final ResultActions result = mockMvc.perform(
            get("/goals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
            .andDo(
                document("goal-find-finish",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상태 값: \"FINISH\""),
                        fieldWithPath("date").type(JsonFieldType.STRING).description("요청 날짜")
                    ),
                    responseFields(fieldWithPath("[]").description("완료한 목표 리스트"))
                        .andWithPrefix("[].",
                            fieldWithPath("id").type(JsonFieldType.NUMBER).description("목표 ID"),
                            fieldWithPath("name").type(JsonFieldType.STRING).description("목표 명"),
                            fieldWithPath("durationType").type(JsonFieldType.STRING).description("목표 기간 타입"),
                            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("목표 금액"),
                            fieldWithPath("dayLeft").type(JsonFieldType.NUMBER).description("0으로 고정"),
                            fieldWithPath("monthLeft").type(JsonFieldType.NUMBER).description("0으로 고정"),
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
            get("/goals/ids")
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
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

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
