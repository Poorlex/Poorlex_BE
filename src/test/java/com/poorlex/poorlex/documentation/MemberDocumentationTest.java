package com.poorlex.poorlex.documentation;

import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.user.member.controller.MemberCommandController;
import com.poorlex.poorlex.user.member.service.MemberCommandService;
import com.poorlex.poorlex.user.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MemberCommandController.class)
class MemberDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberCommandService memberCommandService;

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
}
