package com.poorlex.poorlex.documentation;

import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.point.controller.MemberPointCommandController;
import com.poorlex.poorlex.user.point.controller.MemberPointQueryController;
import com.poorlex.poorlex.user.point.service.MemberPointCommandService;
import com.poorlex.poorlex.user.point.service.MemberPointQueryService;
import com.poorlex.poorlex.user.point.service.dto.request.PointCreateRequest;
import com.poorlex.poorlex.user.point.service.dto.response.MemberLevelBarResponse;
import com.poorlex.poorlex.user.point.service.dto.response.MemberPointAndLevelResponse;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MemberPointCommandController.class, MemberPointQueryController.class})
class MemberPointDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberPointCommandService memberPointService;

    @MockBean
    private MemberPointQueryService memberPointQueryService;

    @Test
    void create() throws Exception {
        //given
        final PointCreateRequest request = new PointCreateRequest(1000);

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(memberPointService).createPoint(anyLong(), anyInt());

        //when
        final ResultActions result = mockMvc.perform(
                post("/point")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        //then
        result.andExpect(status().isCreated())
                .andDo(
                        document("member-point-create",
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
        given(memberPointQueryService.findMemberSumPointAndLevel(any()))
                .willReturn(new MemberPointAndLevelResponse(1000, MemberLevel.LEVEL_4.getNumber()));

        //when
        final ResultActions result = mockMvc.perform(
                get("/point")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("member-total-point-find",
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
        given(memberPointQueryService.findMemberLevelBarInfo(any()))
                .willReturn(new MemberLevelBarResponse(MemberLevel.LEVEL_1.getLevelRange(), 10, 10));

        //when
        final ResultActions result = mockMvc.perform(
                get("/point/level-bar")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("member-level-bar",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("levelRange").type(JsonFieldType.NUMBER)
                                                 .description("래밸 구간의 길이"),
                                         fieldWithPath("currentPoint").type(JsonFieldType.NUMBER)
                                                 .description("현재 레벨 도달 이후 얻은 총 포인트"),
                                         fieldWithPath("recentPoint").type(JsonFieldType.NUMBER)
                                                 .description("가장 최근에 얻은 포인트")
                                 )
                        ));
    }
}
