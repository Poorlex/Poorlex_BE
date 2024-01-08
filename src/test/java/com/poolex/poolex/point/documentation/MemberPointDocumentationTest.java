package com.poolex.poolex.point.documentation;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.MemberLevel;
import com.poolex.poolex.point.controller.MemberPointController;
import com.poolex.poolex.point.service.MemberPointService;
import com.poolex.poolex.point.service.dto.request.PointCreateRequest;
import com.poolex.poolex.point.service.dto.response.MemberPointResponse;
import com.poolex.poolex.support.RestDocsDocumentationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberPointController.class)
class MemberPointDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberPointService memberPointService;

    @Test
    void create() throws Exception {
        //given
        final PointCreateRequest request = new PointCreateRequest(1000);

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(memberPointService).createPoint(anyLong(), anyInt());

        //when
        final ResultActions result = mockMvc.perform(
            post("/points")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isCreated())
            .andDo(
                document("member-point-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("point").type(JsonFieldType.NUMBER).description("멤버 지급 포인트")
                    )
                ));
    }

    @Test
    void find_total_point_and_level() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(memberPointService.findMemberSumPoint(any()))
            .willReturn(new MemberPointResponse(1000, MemberLevel.LEVEL_4.getNumber()));

        //when
        final ResultActions result = mockMvc.perform(
            get("/points")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("member-total-point-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("totalPoint").type(JsonFieldType.NUMBER).description("멤버 총 포인트"),
                        fieldWithPath("level").type(JsonFieldType.NUMBER).description("멤버 레벨")
                    )
                ));
    }
}
