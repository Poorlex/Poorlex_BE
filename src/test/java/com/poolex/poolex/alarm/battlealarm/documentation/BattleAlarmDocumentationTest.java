package com.poolex.poolex.alarm.battlealarm.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.alarm.battlealarm.controller.BattleAlarmController;
import com.poolex.poolex.alarm.battlealarm.service.BattleAlarmService;
import com.poolex.poolex.alarm.battlealarm.service.dto.request.BattleAlarmRequest;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.BattleAlarmResponse;
import com.poolex.poolex.alarm.battlealarm.service.dto.response.UncheckedBattleAlarmCountResponse;
import com.poolex.poolex.support.RestDocsDocumentationTest;
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
class BattleAlarmDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleAlarmService battleAlarmService;

    @Test
    void find_battle_alarm() throws Exception {
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
                document("battle-alarm-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("dateTime").type(JsonFieldType.STRING).description("요청 시간 [ yyyy-mm-ddThh:mm ]")
                    ),
                    responseFields()
                        .andWithPrefix("[].",
                            fieldWithPath("alarmId").type(JsonFieldType.NUMBER).description("알림 Id"),
                            fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("알림 생성 멤버 Id"),
                            fieldWithPath("alarmType").type(JsonFieldType.STRING).description("알림 타입"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                .description("알림 생성 시간 [ yyyy-mm-ddThh:mm ]")
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
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("count").type(JsonFieldType.NUMBER).description("회원이 읽지 않은 배틀 알림 수")
                    )
                ));
    }
}
