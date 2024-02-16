package com.poorlex.poorlex.alarm.battlealarm.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.alarm.battlealarm.controller.BattleAlarmController;
import com.poorlex.poorlex.alarm.battlealarm.service.BattleAlarmService;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poorlex.poorlex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poorlex.poorlex.battlealarmreaction.service.dto.response.AlarmReactionResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import com.poorlex.poorlex.voting.vote.service.dto.response.VoteResponse;
import com.poorlex.poorlex.voting.votingpaper.service.dto.response.VotingPaperResponse;
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

@WebMvcTest(BattleAlarmController.class)
class BattleAlarmDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleAlarmService battleAlarmService;

    @Test
    void find_battle_alarm_basic() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        given(battleAlarmService.findBattleAlarms(any(), any(), any())).willReturn(
            List.of(
                new BattleAlarmResponse(1L, 1L, "EXPENDITURE_CREATED",
                    LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)),
                new BattleAlarmResponse(1L, 1L, "BATTLE_NOTIFICATION_CHANGED",
                    LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
            )
        );

        //when
        final ResultActions result = mockMvc.perform(
            get("/battles/1/alarms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-alarm-find-basic",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("요청 시간")
                    ),
                    responseFields(fieldWithPath("[]").description("배틀 알림 리스트 ( 기본 )"))
                        .andWithPrefix("[].",
                            fieldWithPath("alarmId").type(JsonFieldType.NUMBER).description("알림 Id"),
                            fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("알림 생성 멤버 Id"),
                            fieldWithPath("alarmType").type(JsonFieldType.STRING).description("알림 타입"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                .description("알림 생성 시간")
                        )
                ));
    }

    @Test
    void find_battle_alarm_vote() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        given(battleAlarmService.findBattleAlarms(any(), any(), any())).willReturn(
            List.of(
                new VoteResponse(1L, "투표 생성자 닉네임", "투표명", "PROGRESS", 5000, 1, 2,
                    LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
            )
        );

        //when
        final ResultActions result = mockMvc.perform(
            get("/battles/1/alarms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-alarm-find-vote",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("요청 시간")
                    ),
                    responseFields(fieldWithPath("[]").description("배틀 알림 리스트 ( 투표 )"))
                        .andWithPrefix("[].",
                            fieldWithPath("alarmType").type(JsonFieldType.STRING).description("알림 타입"),
                            fieldWithPath("voteId").type(JsonFieldType.NUMBER).description("투표 Id"),
                            fieldWithPath("voteMakerNickname").type(JsonFieldType.STRING).description("투표 생성 참가자 닉네임"),
                            fieldWithPath("voteName").type(JsonFieldType.STRING).description("투표명"),
                            fieldWithPath("voteStatus").type(JsonFieldType.STRING).description("투표 상태"),
                            fieldWithPath("voteAmount").type(JsonFieldType.NUMBER).description("투표 금액"),
                            fieldWithPath("voteAgreeCount").type(JsonFieldType.NUMBER).description("투표 찬성 표 수"),
                            fieldWithPath("voteDisagreeCount").type(JsonFieldType.NUMBER).description("투표 반대 표 수"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("투표 생성 시간")
                        )
                ));
    }

    @Test
    void find_battle_alarm_voting_paper() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        given(battleAlarmService.findBattleAlarms(any(), any(), any())).willReturn(
            List.of(
                new VotingPaperResponse("투표한 투표명", 5000, true,
                    LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
            )
        );

        //when
        final ResultActions result = mockMvc.perform(
            get("/battles/1/alarms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-alarm-find-voting-paper",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("요청 시간")
                    ),
                    responseFields(fieldWithPath("[]").description("배틀 알림 리스트 ( 투표 참가 표 )"))
                        .andWithPrefix("[].",
                            fieldWithPath("alarmType").type(JsonFieldType.STRING).description("알림 타입"),
                            fieldWithPath("voteName").type(JsonFieldType.STRING).description("투표명"),
                            fieldWithPath("voteAmount").type(JsonFieldType.NUMBER).description("투표 금액"),
                            fieldWithPath("agree").type(JsonFieldType.BOOLEAN).description("투표 찬성 여부"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("투표 참가표 생성 시간")
                        )
                ));
    }

    @Test
    void find_battle_alarm_reaction() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        final BattleAlarmRequest request = new BattleAlarmRequest(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        given(battleAlarmService.findBattleAlarms(any(), any(), any())).willReturn(
            List.of(
                new AlarmReactionResponse("PRAISE", "반응 내용",
                    LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
            )
        );

        //when
        final ResultActions result = mockMvc.perform(
            get("/battles/1/alarms")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-alarm-find-reaction",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("요청 시간")
                    ),
                    responseFields(fieldWithPath("[]").description("배틀 알림 리스트 ( 알림 반응 )"))
                        .andWithPrefix("[].",
                            fieldWithPath("alarmType").type(JsonFieldType.STRING).description("알림 타입"),
                            fieldWithPath("alarmReactionType").type(JsonFieldType.STRING)
                                .description("알림 반응 타입 ( 'PRAISE(칭찬하기)', 'SCOLD(혼내기)' "),
                            fieldWithPath("alarmReactionContent").type(JsonFieldType.STRING).description("반응 내용"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("알림 반응 생성 시간")
                        )
                ));
    }

    @Test
    void find_unchecked_battle_alarm_count() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleAlarmService.getBattleParticipantUncheckedAlarmCount(any(), any()))
            .willReturn(new UncheckedBattleAlarmCountResponse(1));

        //when
        final ResultActions result = mockMvc.perform(
            get("/battles/1/alarms/unchecked")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-unchecked-alarm-count",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    responseFields(
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("회원이 읽지 않은 배틀 알림 수")
                    )
                ));
    }
}
