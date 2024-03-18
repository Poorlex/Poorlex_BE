package com.poorlex.poorlex.alarm.memberalram.documentation;

import com.poorlex.poorlex.alarm.memberalram.controller.MemberAlarmController;
import com.poorlex.poorlex.alarm.memberalram.service.MemberAlarmService;
import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberAlarmController.class)
class MemberAlarmDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberAlarmService memberAlarmService;

    @Test
    void find_member_alarms() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(memberAlarmService.findMemberAlarmsByCurrentTime(any())).willReturn(
                List.of(
                        new MemberAlarmResponse(1L, "관련 멤버 닉네임", "관련 배틀 이름", "FRIEND_INVITATION", 60 * 3, 3, 0),
                        new MemberAlarmResponse(1L, "관련 멤버 닉네임", "관련 배틀 이름", "BATTLE_INVITATION", 60 * 25, 25, 1)
                )
        );

        //when
        final ResultActions result = mockMvc.perform(
                get("/member/alarms")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("member-alarm-find",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(fieldWithPath("[]").description("마이페이지 알림 리스트"))
                                         .andWithPrefix("[].",
                                                        fieldWithPath("alarmId").type(NUMBER).description("알림 Id"),
                                                        fieldWithPath("friendName").type(STRING)
                                                                .description("알림 관련 멤버 닉네임")
                                                                .optional(),
                                                        fieldWithPath("battleName").type(STRING)
                                                                .description("알림 관련 배틀명")
                                                                .optional(),
                                                        fieldWithPath("alarmType").type(STRING).description("알림 타입"),
                                                        fieldWithPath("minutePassed").type(NUMBER)
                                                                .description("알림 생성 후 소요된 분"),
                                                        fieldWithPath("hourPassed").type(NUMBER)
                                                                .description("알림 생성 후 소요된 시간"),
                                                        fieldWithPath("dayPassed").type(NUMBER)
                                                                .description("알림 생성 후 소요된 일")
                                         )
                        ));
    }
}
