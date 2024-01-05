package com.poolex.poolex.alarm.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.alarm.controller.AlarmController;
import com.poolex.poolex.alarm.service.AlarmService;
import com.poolex.poolex.alarm.service.dto.request.BattleAlarmResponse;
import com.poolex.poolex.support.RestDocsDocumentationTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AlarmController.class)
class AlarmDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlarmService alarmService;

    @Test
    void find_battle_alarm() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(alarmService.findBattleAlarms(any())).willReturn(
            List.of(
                new BattleAlarmResponse(1L, 1L, "EXPENDITURE_CREATED", LocalDateTime.now()),
                new BattleAlarmResponse(1L, 1L, "BATTLE_NOTIFICATION_CHANGED", LocalDateTime.now())
            )
        );

        //when
        final ResultActions result = mockMvc.perform(get("/battles/1/alarms"));

        //then
        result.andExpect(status().isOk())
            .andDo(
                document("battle-alarm-find",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
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
}
