package com.poorlex.poorlex.battle.invitation.documentation;

import com.poorlex.poorlex.battle.invitation.controller.BattleInviteController;
import com.poorlex.poorlex.battle.invitation.service.BattleInviteService;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteAcceptRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteDenyRequest;
import com.poorlex.poorlex.battle.invitation.service.dto.request.BattleInviteRequest;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BattleInviteController.class)
class BattleInviteDocumentationTest extends MockMvcTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleInviteService battleInviteService;

    @Test
    void 배틀에_초대한다() throws Exception {
        //given
        doNothing().when(battleInviteService).invite(any(), any(), any());

        final BattleInviteRequest request = new BattleInviteRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
                post("/battles/{battleId}/invite", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );
        result.andExpect(status().isCreated())
                .andDo(
                        document("battle-invite",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("invitedMemberId").type(JsonFieldType.NUMBER)
                                                 .description("배틀에 초대할 멤버 Id")
                                 )
                        )
                );
    }

    @Test
    void 배틀초대_요청을_수락한다() throws Exception {
        //given
        doNothing().when(battleInviteService).inviteAccept(any(), any());

        final BattleInviteAcceptRequest request = new BattleInviteAcceptRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
                post("/battle-invite/accept")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );
        result.andExpect(status().isOk())
                .andDo(
                        document("battle-invite-accept",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("inviteBattleParticipantId").type(JsonFieldType.NUMBER)
                                                 .description("배틀에 초대한 배틀 참가자 Id")
                                 )
                        )
                );
    }

    @Test
    void 배틀초대_요청을_거절한다() throws Exception {
        //given
        doNothing().when(battleInviteService).inviteDeny(any(), any());

        final BattleInviteDenyRequest request = new BattleInviteDenyRequest(1L);

        //when
        //then
        final ResultActions result = mockMvc.perform(
                post("/battle-invite/deny")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );
        result.andExpect(status().isOk())
                .andDo(
                        document("battle-invite-deny",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("inviteBattleParticipantId").type(JsonFieldType.NUMBER)
                                                 .description("배틀에 초대한 배틀 참가자 Id")
                                 )
                        )
                );
    }
}
