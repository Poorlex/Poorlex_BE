package com.poorlex.poorlex.battle.documentation;

import com.poorlex.poorlex.battle.controller.BattleController;
import com.poorlex.poorlex.battle.service.BattleService;
import com.poorlex.poorlex.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.service.dto.response.BattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.FindingBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.battle.service.dto.response.ParticipantRankingResponse;
import com.poorlex.poorlex.support.MockMultipartFileFixture;
import com.poorlex.poorlex.support.MockMvcTest;
import com.poorlex.poorlex.util.ApiDocumentUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BattleController.class)
class BattleDocumentationTest extends MockMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private BattleService battleService;

    @Test
    void create() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleService.create(any(), any(), any())).willReturn(1L);

        //when
        final ResultActions result = mockMvc.perform(
                multipart(HttpMethod.POST, "/battles")
                        .file(MockMultipartFileFixture.get())
                        .queryParam("name", "배틀 이름")
                        .queryParam("introduction", "배틀 소개")
                        .queryParam("budget", "10000")
                        .queryParam("maxParticipantSize", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(
                        document("battle-create",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 queryParameters(
                                         parameterWithName("name").description("배틀 이름"),
                                         parameterWithName("introduction").description("배틀 설명"),
                                         parameterWithName("budget").description("배틀 예산 ( Long )"),
                                         parameterWithName("maxParticipantSize").description("배틀 최대 참가자 수 ( Integer )")
                                 ),
                                 requestParts(
                                         partWithName("image").description("배틀 이미지")
                                 )
                        ));
    }

    @Test
    void find_recruiting() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleService.findBattlesToPlay()).willReturn(
                List.of(
                        new FindingBattleResponse(1L, "첫번째 배틀명", "첫번째 배틀 이미지 링크", "HARD", 10000, 2, 10),
                        new FindingBattleResponse(2L, "두번째 배틀명", "두번째 배틀 이미지 링크", "NORMAL", 90000, 1, 10),
                        new FindingBattleResponse(3L, "세번째 배틀명", "세번째 배틀 이미지 링크", "EASY", 150000, 5, 10)
                )
        );

        //when
        final ResultActions result = mockMvc.perform(
                get("/battles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("battle-find-recruiting",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(fieldWithPath("[]").description("모집중 배틀방 리스트"))
                                         .andWithPrefix("[].",
                                                        fieldWithPath("battleId").type(JsonFieldType.NUMBER)
                                                                .description("배틀 ID"),
                                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                                .description("배틀방 이름"),
                                                        fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                                .description("배틀 이미지 링크"),
                                                        fieldWithPath("budget").type(JsonFieldType.NUMBER)
                                                                .description("배틀 예산"),
                                                        fieldWithPath("difficulty").type(JsonFieldType.STRING)
                                                                .description("배틀 난이도"),
                                                        fieldWithPath("currentParticipant").type(JsonFieldType.NUMBER)
                                                                .description("배틀 현재 참가자 수"),
                                                        fieldWithPath("maxParticipantCount").type(JsonFieldType.NUMBER)
                                                                .description("배틀 최대 참가자 수")
                                         )
                        ));
    }

    @Test
    void find_progressing() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleService.findProgressMemberBattles(any(), any())).willReturn(
                List.of(
                        new MemberProgressBattleResponse(1L, "첫번째 배틀명", "첫번째 배틀 이미지 링크", "HARD", 5, 10000, 1, 10, 1),
                        new MemberProgressBattleResponse(2L, "두번째 배틀명", "두번째 배틀 이미지 링크", "NORMAL", 5, 90000, 1, 10, 1),
                        new MemberProgressBattleResponse(3L, "세번째 배틀명", "세번째 배틀 이미지 링크", "EASY", 5, 150000, 1, 10, 1)
                )
        );

        //when
        final ResultActions result = mockMvc.perform(get("/battles/progress")
                                                             .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("battle-find-participated",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(fieldWithPath("[]").description("참가중 배틀방 리스트"))
                                         .andWithPrefix("[].",
                                                        fieldWithPath("battleId").type(JsonFieldType.NUMBER)
                                                                .description("배틀 ID"),
                                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                                .description("배틀방 이름"),
                                                        fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                                .description("배틀방 이미지 링크"),
                                                        fieldWithPath("difficulty").type(JsonFieldType.STRING)
                                                                .description("배틀 난이도"),
                                                        fieldWithPath("dday").type(JsonFieldType.NUMBER)
                                                                .description("배틀 종료 D-Day"),
                                                        fieldWithPath("budgetLeft").type(JsonFieldType.NUMBER)
                                                                .description("배틀 예산에서 멤버의 지출을 뺀 비용"),
                                                        fieldWithPath("currentParticipantRank").type(JsonFieldType.NUMBER)
                                                                .description("배틀에서 멤버의 랭킹"),
                                                        fieldWithPath("battleParticipantCount").type(JsonFieldType.NUMBER)
                                                                .description("현재 배틀 참가자 수"),
                                                        fieldWithPath("uncheckedAlarmCount").type(JsonFieldType.NUMBER)
                                                                .description("미확인 배틀 알림 수")
                                         )
                        ));
    }

    @Test
    void find_complete() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleService.findCompleteMemberBattles(any(), any())).willReturn(
                List.of(
                        new MemberCompleteBattleResponse(1L, "첫번째 배틀명", "첫번째 배틀 이미지 링크", "HARD", 5, 10000, 1, 10, 30),
                        new MemberCompleteBattleResponse(2L, "두번째 배틀명", "두번째 배틀 이미지 링크", "NORMAL", 5, 90000, 1, 10, 30),
                        new MemberCompleteBattleResponse(3L, "세번째 배틀명", "세번째 배틀 이미지 링크", "EASY", 5, 150000, 1, 10, 30)
                )
        );

        //when
        final ResultActions result = mockMvc.perform(get("/battles/complete")
                                                             .header(HttpHeaders.AUTHORIZATION, "Bearer {accessToken}")
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("battle-find-complete",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 responseFields(fieldWithPath("[]").description("종료된 배틀방 리스트"))
                                         .andWithPrefix("[].",
                                                        fieldWithPath("battleId").type(JsonFieldType.NUMBER)
                                                                .description("배틀 ID"),
                                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                                .description("배틀방 이름"),
                                                        fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                                                .description("배틀방 이미지 링크"),
                                                        fieldWithPath("difficulty").type(JsonFieldType.STRING)
                                                                .description("배틀 난이도"),
                                                        fieldWithPath("pastDay").type(JsonFieldType.NUMBER)
                                                                .description("배틀 종료 후 지난 일 수"),
                                                        fieldWithPath("budgetLeft").type(JsonFieldType.NUMBER)
                                                                .description("배틀 예산에서 멤버의 지출을 뺀 비용"),
                                                        fieldWithPath("earnedPoint").type(JsonFieldType.NUMBER)
                                                                .description("배틀에서 멤버가 얻은 포인트의 양"),
                                                        fieldWithPath("currentParticipantRank").type(JsonFieldType.NUMBER)
                                                                .description("배틀에서 멤버의 랭킹"),
                                                        fieldWithPath("battleParticipantCount").type(JsonFieldType.NUMBER)
                                                                .description("배틀 참가자 수")
                                         )
                        ));
    }

    @Test
    void find_one_battle() throws Exception {
        //given
        mockingTokenInterceptor();
        mockingMemberArgumentResolver();
        given(battleService.getBattleInfo(any(), any()))
                .willReturn(new BattleResponse(
                                    "배틀명",
                                    10,
                                    10,
                                    10000,
                                    5,
                                    List.of(
                                            new ParticipantRankingResponse(1, 1, true, "참가자 닉네임1", 1000L),
                                            new ParticipantRankingResponse(1, 2, false, "참가자 닉네임2", 1000L),
                                            new ParticipantRankingResponse(3, 3, false, "참가자 닉네임3", 2000L)
                                    )
                            )
                );
        final BattleFindRequest request = new BattleFindRequest(LocalDate.now());

        //when
        final ResultActions result = mockMvc.perform(get("/battles/{battleId}", 1)
                                                             .content(objectMapper.writeValueAsString(request))
                                                             .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        document("battle-find-by-id",
                                 ApiDocumentUtils.getDocumentRequest(),
                                 ApiDocumentUtils.getDocumentResponse(),
                                 requestFields(
                                         fieldWithPath("date").type(JsonFieldType.STRING).description("조회 날짜")
                                 ),
                                 responseFields(
                                         fieldWithPath("battleName").type(JsonFieldType.STRING).description("배틀 명"),
                                         fieldWithPath("maxParticipantSize").type(JsonFieldType.NUMBER)
                                                 .description("배틀 최대 참가자 수"),
                                         fieldWithPath("currentParticipantSize").type(JsonFieldType.NUMBER)
                                                 .description("현재 배틀 참가자 수"),
                                         fieldWithPath("battleBudget").type(JsonFieldType.NUMBER).description("배틀 예산"),
                                         fieldWithPath("battleDDay").type(JsonFieldType.NUMBER)
                                                 .description("배틀 종료까지 D-Day"),
                                         fieldWithPath(".rankings[]").description("배틀 참가자 랭킹 리스트")
                                 ).andWithPrefix(".rankings[].",
                                                 fieldWithPath("rank").type(JsonFieldType.NUMBER).description("참가자 랭킹"),
                                                 fieldWithPath("level").type(JsonFieldType.NUMBER)
                                                         .description("참자가 레벨"),
                                                 fieldWithPath("manager").type(JsonFieldType.BOOLEAN)
                                                         .description("참가자 매니저 여부"),
                                                 fieldWithPath("nickname").type(JsonFieldType.STRING)
                                                         .description("참가자 닉네임"),
                                                 fieldWithPath("expenditure").type(JsonFieldType.NUMBER)
                                                         .description("참가자 지출")
                                 )
                        ));
    }
}
