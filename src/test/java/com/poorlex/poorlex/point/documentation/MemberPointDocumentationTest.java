package com.poorlex.poorlex.point.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.point.controller.MemberPointController;
import com.poorlex.poorlex.point.service.MemberPointService;
import com.poorlex.poorlex.point.service.dto.request.PointCreateRequest;
import com.poorlex.poorlex.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.point.service.dto.response.MemberPointResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
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
class MemberPointDocumentationTest extends MockMvcTest {

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
                .with(csrf())
        );

        //then
        result.andExpect(status().isCreated())
            .andDo(
                document("user-point-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
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
        given(memberPointService.findMemberTotalPoint(any()))
            .willReturn(new MemberPointResponse(1000, MemberLevel.LEVEL_4.getNumber()));

        //when
        final ResultActions result = mockMvc.perform(
            get("/points")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("user-total-point-find",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    responseFields(
                        fieldWithPath("totalPoint").type(JsonFieldType.NUMBER).description("멤버 총 포인트"),
                        fieldWithPath("level").type(JsonFieldType.NUMBER).description("멤버 레벨")
                    )
                ));
    }

    @Test
    void find_info_for_level_bar() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(memberPointService.findPointsForLevelBar(any()))
            .willReturn(new MemberLevelBarResponse(MemberLevel.LEVEL_1.getLevelRange(), 10, 10));

        //when
        final ResultActions result = mockMvc.perform(
            get("/points/level-bar")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("user-level-bar",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    responseFields(
                        fieldWithPath("levelRange").type(JsonFieldType.NUMBER).description("래밸 구간의 길이"),
                        fieldWithPath("currentPoint").type(JsonFieldType.NUMBER).description("현재 레벨 도달 이후 얻은 총 포인트"),
                        fieldWithPath("recentPoint").type(JsonFieldType.NUMBER).description("가장 최근에 얻은 포인트")
                    )
                ));
    }
}
