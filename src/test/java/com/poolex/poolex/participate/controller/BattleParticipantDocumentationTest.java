package com.poolex.poolex.participate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.poolex.poolex.participate.service.BattleParticipantService;
import com.poolex.poolex.support.RestDocsDocumentationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BattleParticipantController.class)
class BattleParticipantDocumentationTest extends RestDocsDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BattleParticipantService battleParticipantService;

    @Test
    void join() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleParticipantService.create(any(), any())).willReturn(1L);

        //when
        final ResultActions result = mockMvc.perform(
            post("/battles/1/participants")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                document("battle-participate",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void join_cancel() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(battleParticipantService).remove(any(), any());

        //when
        final ResultActions result = mockMvc.perform(
            delete("/battles/1/participants")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(
                document("battle-participate-cancel",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                ));
    }
}
