package com.poorlex.poorlex.documentation;

import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.user.member.controller.MemberCommandController;
import com.poorlex.poorlex.user.member.controller.MemberQueryController;
import com.poorlex.poorlex.user.member.service.MemberCommandService;
import com.poorlex.poorlex.user.member.service.MemberQueryService;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.user.member.service.dto.response.MemberProfileResponse;
import com.poorlex.poorlex.user.point.service.dto.response.MyPageLevelInfoResponse;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest({MemberCommandController.class, MemberQueryController.class})
class MemberDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberCommandService memberCommandService;

    @MockBean
    private MemberQueryService memberQueryService;

    @Test
    void member_profile_update() throws Exception {
        //given
        doNothing().when(memberCommandService).updateProfile(any(), any());
        final MemberProfileUpdateRequest request = new MemberProfileUpdateRequest("변경할 닉네임", "변경할 소개글");

        //when
        //then
        final ResultActions result = mockMvc.perform(
                patch("/member/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("member-profile-update",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("nickname").type(JsonFieldType.STRING)
                                                 .description("변경할 닉네임")
                                                 .optional(),
                                         fieldWithPath("description").type(JsonFieldType.STRING)
                                                 .description("변경할 소개글")
                                                 .optional()
                                 )
                        )
                );
    }

    @Test
    void member_profile() throws Exception {
        //given
        given(memberQueryService.getMemberProfile(any())).willReturn(new MemberProfileResponse("멤버닉네임", "멤버 소개", new MyPageLevelInfoResponse(4, 400, 400)));

        //when
        //then
        final ResultActions result = mockMvc.perform(
                get("/member/{memberId}/profile", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("member-profile",
                                ApiDocumentUtils.getDocumentRequest(),
                                ApiDocumentUtils.getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("description").type(JsonFieldType.STRING)
                                                .description("소개글")
                                ).andWithPrefix(".levelInfo.",
                                        fieldWithPath("level").type(JsonFieldType.NUMBER)
                                                .description("레벨"),
                                        fieldWithPath("point").type(JsonFieldType.NUMBER)
                                                .description("포인트"),
                                        fieldWithPath("pointLeftForLevelUp").type(JsonFieldType.NUMBER)
                                                .description("레벨업까지 남은 포인트")
                                )
                        ));
    }
}
