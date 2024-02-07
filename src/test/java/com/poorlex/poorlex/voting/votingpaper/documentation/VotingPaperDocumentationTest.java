package com.poorlex.poorlex.voting.votingpaper.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.RestDocsDocumentationTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import com.poorlex.poorlex.voting.votingpaper.controller.VotingPaperController;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import com.poorlex.poorlex.voting.votingpaper.service.dto.request.VotingPaperCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(VotingPaperController.class)
class VotingPaperDocumentationTest extends RestDocsDocumentationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotingPaperService votingPaperService;

    @Test
    void 투표에_투표한다() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        doNothing().when(votingPaperService).createVotingPaper(any(), any(), any());
        final VotingPaperCreateRequest request = new VotingPaperCreateRequest(true);

        //when
        //then
        final ResultActions result = mockMvc.perform(
            post("/battles/1/votes/1/vote")
                .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isCreated())
            .andDo(
                document("battle-voting-paper-create",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestFields(
                        fieldWithPath("agree").type(JsonFieldType.BOOLEAN).description("투표 찬성 여부")
                    )
                )
            );
    }
}
