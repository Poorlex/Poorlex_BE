package com.poorlex.poorlex.voting.vote.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import com.poorlex.poorlex.voting.vote.controller.VoteController;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(VoteController.class)
class VoteDocumentationTest extends MockMvcTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Test
    void 투표를_생성한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(voteService).createVote(any(), any(), any());
        final VoteCreateRequest request = new VoteCreateRequest(1500, LocalDateTime.now(), 30, "초코우유");

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/battles/1/votes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
        );

        result.andExpect(status().isCreated())
            .andDo(
                document("battle-vote-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("amount").type(JsonFieldType.NUMBER).description("투표 품목 금액"),
                        fieldWithPath("start").type(JsonFieldType.STRING).description("투표 시작 시간"),
                        fieldWithPath("duration").type(JsonFieldType.NUMBER).description("투표 기간"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("투표 품목")
                    )
                )
            );
    }
}
