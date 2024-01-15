package com.poolex.poolex.goal.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.goal.controller.GoalController;
import com.poolex.poolex.goal.domain.GoalType;
import com.poolex.poolex.goal.service.GoalService;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
import com.poolex.poolex.support.RestDocsDocumentationTest;
import java.time.LocalDate;
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
                document("create-goal",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
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
}
