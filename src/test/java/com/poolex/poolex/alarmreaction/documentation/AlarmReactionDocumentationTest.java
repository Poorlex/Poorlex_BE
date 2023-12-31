package com.poolex.poolex.alarmreaction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.alarmreaction.controller.AlarmReactionController;
import com.poolex.poolex.alarmreaction.service.AlarmReactionService;
import com.poolex.poolex.alarmreaction.service.dto.request.AlarmReactionCreateRequest;
import com.poolex.poolex.support.RestDocsDocumentationTest;
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
class AlarmReactionDocumentationTest extends RestDocsDocumentationTest {

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
        );

        //then
        result.andExpect(status().isCreated())
            .andDo(
                document("alarm-reaction-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("alarmId").type(JsonFieldType.NUMBER).description("알림 Id"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description("알림 반응 타입 [ 칭찬하기, 혼내기 ]"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("알림 반응 문구")
                    )
                ));
    }
}
