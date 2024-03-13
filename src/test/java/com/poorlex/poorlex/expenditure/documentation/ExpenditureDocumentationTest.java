package com.poorlex.poorlex.expenditure.documentation;

import com.poorlex.poorlex.expenditure.controller.ExpenditureController;
import com.poorlex.poorlex.expenditure.service.ExpenditureCommandService;
import com.poorlex.poorlex.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenditureController.class)
class ExpenditureDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenditureQueryService expenditureQueryService;

    @MockBean
    private ExpenditureCommandService expenditureCommandService;

    @Test
    void create() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(expenditureCommandService.createExpenditure(any(), any(), any(), any())).willReturn(1L);

        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        final ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/expenditures")
                        .file(mainImage)
                        .file(subImage)
                        .queryParam("amount", "1000")
                        .queryParam("description", "소개")
                        .queryParam("date", LocalDate.now().toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .with(csrf())
        );

        //then
        result.andExpect(status().isCreated())
                .andDo(
                        document("expenditure-create",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 queryParameters(
                                         parameterWithName("amount").description("금액"),
                                         parameterWithName("description").description("설명"),
                                         parameterWithName("date").description("지출 날짜")
                                 ),
                                 requestParts(
                                         partWithName("mainImage").description("지출 메인 이미지"),
                                         partWithName("subImage").description("지출 서브 이미지").optional()
                                 )
                        ));
    }

//    @Test
//    void update() throws Exception {
//        //given
//        mockingTokenInterceptor();
//        mockingMemberArgumentResolver();
//        doNothing().when(expenditureService).updateExpenditure(any(), any(), any());
//        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(
//            1000,
//            "지출 설명",
//            List.of("지출 이미지 링크")
//        );
//
//        //when
//        final ResultActions result = mockMvc.perform(
//            patch("/expenditures/1")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(csrf())
//        );
//
//        //then
//        result.andExpect(status().isOk())
//            .andDo(
//                document("expenditure-update",
//                    ApiDocumentUtils.getDocumentRequest(),
//                    ApiDocumentUtils.getDocumentResponse(),
//                    requestFields(
//                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("수정할 지출 금액"),
//                        fieldWithPath("description").type(JsonFieldType.STRING).description("수정할 지출 설명"),
//                        fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("수정할 지출 이미지 목록 (최대 2개)")
//                    )
//                ));
//    }

    @Test
    void find_weekly_expenditure_with_date() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        final MemberWeeklyTotalExpenditureRequest request = new MemberWeeklyTotalExpenditureRequest(LocalDateTime.now());
        given(expenditureQueryService.findMemberWeeklyTotalExpenditure(any(), any()))
                .willReturn(new MemberWeeklyTotalExpenditureResponse(1000));

        //when
        final ResultActions result = mockMvc.perform(
                get("/expenditures/weekly?withDate=true")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-weekly-total-with-date",
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

    @Test
    void find_weekly_expenditure() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        given(expenditureQueryService.findMemberCurrentWeeklyTotalExpenditure(any()))
                .willReturn(new MemberWeeklyTotalExpenditureResponse(1000));

        //when
        final ResultActions result = mockMvc.perform(
                get("/expenditures/weekly")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-weekly-total",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("amount").type(JsonFieldType.NUMBER).description("주간 총 지출 금액")
                                 )
                        ));
    }

    @Test
    void find_expenditure() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        given(expenditureQueryService.findExpenditureById(any()))
                .willReturn(
                        new ExpenditureResponse(
                                1L,
                                LocalDate.now(),
                                1000,
                                "지출 설명", "imageUrl1", "imageUrl2"
                        )
                );

        //when
        //then
        final ResultActions result = mockMvc.perform(get("/expenditures/{expenditureId}", 1L));

        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-find",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("id").type(JsonFieldType.NUMBER).description("지출 Id"),
                                         fieldWithPath("date").type(JsonFieldType.STRING).description("지출 날짜"),
                                         fieldWithPath("amount").type(JsonFieldType.NUMBER).description("지출 금액"),
                                         fieldWithPath("description").type(JsonFieldType.STRING).description("지출 설명"),
                                         fieldWithPath("mainImageUrl").type(JsonFieldType.STRING)
                                                 .description("지출 메인 이미지 URL"),
                                         fieldWithPath("subImageUrl").type(JsonFieldType.STRING)
                                                 .description("지출 서브 이미지 URL")
                                                 .optional()
                                 )
                        ));
    }

    @Test
    void find_battle_expenditure_by_dayOfWeek() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        given(expenditureQueryService.findBattleExpendituresInDayOfWeek(any(), any(), any()))
                .willReturn(
                        List.of(
                                new BattleExpenditureResponse(1L, "대표 이미지 URL", 2, true),
                                new BattleExpenditureResponse(2L, "대표 이미지 URL", 1, false),
                                new BattleExpenditureResponse(3L, "대표 이미지 URL", 1, true),
                                new BattleExpenditureResponse(4L, "대표 이미지 URL", 2, false)
                        )
                );

        //when
        //then
        final ResultActions result = mockMvc.perform(
                get("/battles/1/expenditures?dayOfWeek=MONDAY")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-find-battle-dayOfWeek",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("[]").description("배틀 요일별 지출 목록")
                                 ).andWithPrefix("[].",
                                                 fieldWithPath("id").type(JsonFieldType.NUMBER).description("지출 Id"),
                                                 fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                         .description("지출 대표 이미지 링크"),
                                                 fieldWithPath("imageCount").type(JsonFieldType.NUMBER)
                                                         .description("지출 이미지 갯수"),
                                                 fieldWithPath("own").type(JsonFieldType.BOOLEAN)
                                                         .description("멤버 소유 여부")
                                 )
                        ));
    }

    @Test
    void find_member_battle_expenditure() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        given(expenditureQueryService.findMemberBattleExpenditures(any(), any()))
                .willReturn(
                        List.of(
                                new BattleExpenditureResponse(1L, "대표 이미지 URL", 2, true),
                                new BattleExpenditureResponse(2L, "대표 이미지 URL", 1, true),
                                new BattleExpenditureResponse(3L, "대표 이미지 URL", 1, true),
                                new BattleExpenditureResponse(4L, "대표 이미지 URL", 2, true)
                        )
                );

        //when
        //then
        final ResultActions result = mockMvc.perform(
                get("/battles/1/expenditures")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-find-battle-member",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("[]").description("회원 배틀 지출 목록")
                                 ).andWithPrefix("[].",
                                                 fieldWithPath("id").type(JsonFieldType.NUMBER).description("지출 Id"),
                                                 fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                         .description("지출 대표 이미지 링크"),
                                                 fieldWithPath("imageCount").type(JsonFieldType.NUMBER)
                                                         .description("지출 이미지 갯수"),
                                                 fieldWithPath("own").type(JsonFieldType.BOOLEAN)
                                                         .description("고정값 = true")
                                 )
                        ));
    }

    @Test
    void find_member_expenditure() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();

        given(expenditureQueryService.findMemberExpenditures(any()))
                .willReturn(
                        List.of(
                                new ExpenditureResponse(
                                        1L,
                                        LocalDate.now().minusDays(1),
                                        1000,
                                        "지출 설명",
                                        "imageUrl1",
                                        "imageUrl2"
                                ),
                                new ExpenditureResponse(
                                        2L,
                                        LocalDate.now(),
                                        4000,
                                        "지출 설명",
                                        "imageUrl3",
                                        null
                                )
                        )
                );

        //when
        //then
        final ResultActions result = mockMvc.perform(
                get("/expenditures")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        result.andExpect(status().isOk())
                .andDo(
                        document("expenditure-find-member",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("[]").description("회원 지출 목록")
                                 ).andWithPrefix("[].",
                                                 fieldWithPath("id").type(JsonFieldType.NUMBER).description("지출 Id"),
                                                 fieldWithPath("date").type(JsonFieldType.STRING).description("지출 날짜"),
                                                 fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                                         .description("지출 금액"),
                                                 fieldWithPath("description").type(JsonFieldType.STRING)
                                                         .description("지출 설명"),
                                                 fieldWithPath("mainImageUrl").type(JsonFieldType.STRING)
                                                         .description("지출 메인 이미지 URL"),
                                                 fieldWithPath("subImageUrl").type(JsonFieldType.STRING)
                                                         .description("지출 서브 이미지 URL")
                                                         .optional()
                                 )
                        ));
    }
}
