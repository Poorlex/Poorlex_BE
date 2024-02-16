package com.poorlex.poorlex.battlealarmreaction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.battlealarmreaction.controller.AlarmReactionController;
import com.poorlex.poorlex.battlealarmreaction.service.AlarmReactionService;
import com.poorlex.poorlex.battlealarmreaction.service.dto.request.AlarmReactionCreateRequest;
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

@WebMvcTest(AlarmReactionController.class)
class BattleAlarmReactionDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlarmReactionService alarmReactionService;

    @Test
    void create() throws Exception {
        //given
        final AlarmReactionCreateRequest request = new AlarmReactionCreateRequest(1L, "PRAISE", "알림 반응 문구");

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(alarmReactionService).createAlarmReaction(anyLong(), any());

        //when
        final ResultActions result = mockMvc.perform(
            post("/alarm-reaction")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        //then
        result.andExpect(status().isCreated())
            .andDo(
                document("battle-alarm-reaction-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("alarmId").type(JsonFieldType.NUMBER).description("알림 Id"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description("알림 반응 타입 [ 칭찬하기, 혼내기 ]"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("알림 반응 문구")
                    )
                ));
    }
}
