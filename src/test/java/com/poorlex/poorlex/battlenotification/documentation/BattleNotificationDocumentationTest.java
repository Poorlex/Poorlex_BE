package com.poorlex.poorlex.battlenotification.documentation;

import com.poorlex.poorlex.battlenotification.controller.BattleNotificationController;
import com.poorlex.poorlex.battlenotification.service.BattleNotificationService;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.response.BattleNotificationResponse;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BattleNotificationController.class)
class BattleNotificationDocumentationTest extends MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleNotificationService battleNotificationService;

    @Test
    void create() throws Exception {
        //given
        final BattleNotificationCreateRequest request =
                new BattleNotificationCreateRequest("12345678901234567890", "공지 이미지 링크");

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(battleNotificationService).createNotification(anyLong(), anyLong(), any());

        //when
        final ResultActions result = mockMvc.perform(
                post("/battles/1/notification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        //then
        result.andExpect(status().isCreated())
                .andDo(
                        document("battle-notification-create",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("content").type(JsonFieldType.STRING).description("배틀 공지 내용"),
                                         fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                 .description("배틀 공지 이미지 링크")
                                                 .optional()
                                 )
                        ));
    }

    @Test
    void update() throws Exception {
        //given
        final BattleNotificationUpdateRequest request =
                new BattleNotificationUpdateRequest("new 12345678901234567890", null);

        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(battleNotificationService).updateNotification(anyLong(), anyLong(), any());

        //when
        final ResultActions result = mockMvc.perform(
                patch("/battles/1/notification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("battle-notification-update",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("content").type(JsonFieldType.STRING)
                                                 .description("변경할 배틀 공지 내용"),
                                         fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                 .description("변경할 배틀 이미지 링크")
                                                 .optional()
                                 )
                        ));
    }

    @Test
    void find() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleNotificationService.findNotificationByBattleId(anyLong()))
                .willReturn(new BattleNotificationResponse("12345678901234567890", "imageUrl"));

        //when
        final ResultActions result = mockMvc.perform(
                get("/battles/1/notification")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document("battle-notification-find",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(
                                         fieldWithPath("content").type(JsonFieldType.STRING).description("배틀 공지 내용"),
                                         fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                 .description("배틀 이미지 링크")
                                                 .optional()
                                 )
                        ));
    }
}
