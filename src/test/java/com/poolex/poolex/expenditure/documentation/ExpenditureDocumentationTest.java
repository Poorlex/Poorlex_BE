package com.poolex.poolex.expenditure.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.expenditure.controller.ExpenditureController;
import com.poolex.poolex.expenditure.service.ExpenditureService;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poolex.poolex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poolex.poolex.support.RestDocsDocumentationTest;
import com.poolex.poolex.util.ApiDocumentUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

@WebMvcTest(ExpenditureController.class)
class ExpenditureDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenditureService expenditureService;

    @Test
    void create() throws Exception {
        //given
        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            1000,
            "지출 설명",
            List.of("지출 이미지 링크"),
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        );

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(expenditureService.createExpenditure(any(), any())).willReturn(1L);

        //when
        final ResultActions result = mockMvc.perform(
            post("/expenditures")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isCreated())
            .andDo(
                document("expenditure-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("지출 금액"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("지출 설명"),
                        fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("지출 이미지 목록 (최대 2개)"),
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("지출 시간 ")
                    )
                ));
    }

    @Test
    void find_weekly_expenditure() throws Exception {
        //given
        final MemberWeeklyTotalExpenditureRequest request = new MemberWeeklyTotalExpenditureRequest(
            LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(expenditureService.findMemberWeeklyTotalExpenditure(any(), any()))
            .willReturn(new MemberWeeklyTotalExpenditureResponse(1000));

        //when
        final ResultActions result = mockMvc.perform(
            get("/expenditures/weekly")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("expenditure-find",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("조회 시간 ")
                    ),
                    responseFields(
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("주간 총 지출 금액")
                    )
                ));
    }
}
