package com.poolex.poolex.friend.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
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

import com.poolex.poolex.friend.controller.FriendController;
import com.poolex.poolex.friend.service.FriendService;
import com.poolex.poolex.friend.service.dto.request.FriendCreateRequest;
import com.poolex.poolex.friend.service.dto.request.FriendDenyRequest;
import com.poolex.poolex.friend.service.dto.request.FriendInviteRequest;
import com.poolex.poolex.friend.service.dto.response.FriendResponse;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.RestDocsDocumentationTest;
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

@WebMvcTest(FriendController.class)
class FriendDocumentationTest extends RestDocsDocumentationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    @Test
    void 친구요청을_생성한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(friendService).inviteFriend(any(), any());

        final FriendInviteRequest request = new FriendInviteRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/friends/invite")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );
        result.andExpect(status().isOk())
            .andDo(
                document("friend-invite",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("inviteMemberId").type(JsonFieldType.NUMBER).description("친구 요청할 멤버 Id")
                    )
                )
            );
    }

    @Test
    void 친구요청을_수락한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(friendService).createFriend(any(), any());

        final FriendCreateRequest request = new FriendCreateRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/friends/invite/accept")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );
        result.andExpect(status().isCreated())
            .andDo(
                document("friend-invite-accept",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("friendMemberId").type(JsonFieldType.NUMBER).description("친구 요청한 멤버 Id")
                    )
                )
            );
    }

    @Test
    void 친구요청을_거절한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(friendService).inviteDeny(any(), any());

        final FriendDenyRequest request = new FriendDenyRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/friends/invite/deny")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );
        result.andExpect(status().isOk())
            .andDo(
                document("friend-invite-deny",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("inviteMemberId").type(JsonFieldType.NUMBER).description("친구 요청한 멤버 Id")
                    )
                )
            );
    }

    @Test
    void 친구목록을_조회한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(friendService.findMemberFriends(any()))
            .willReturn(List.of(
                new FriendResponse(1, "친구1 닉네임", 1000),
                new FriendResponse(3, "친구2 닉네임", 0),
                new FriendResponse(2, "친구3 닉네임", 13000)
            ));

        //when
        //then
        final ResultActions result = mockMvc.perform(
            get("/friends")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        result.andExpect(status().isOk())
            .andDo(
                document("friend-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields()
                        .andWithPrefix("[].",
                            fieldWithPath("level").type(JsonFieldType.NUMBER).description("친구의 레벨"),
                            fieldWithPath("nickname").type(JsonFieldType.STRING).description("친구의 닉네임"),
                            fieldWithPath("weeklyExpenditure").type(JsonFieldType.NUMBER).description("친구의 주간 총 지출")
                        )
                )
            );
    }
}
