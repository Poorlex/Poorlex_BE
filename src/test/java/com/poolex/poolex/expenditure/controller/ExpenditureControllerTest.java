package com.poolex.poolex.expenditure.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poolex.poolex.auth.domain.Member;
import com.poolex.poolex.auth.domain.MemberNickname;
import com.poolex.poolex.auth.domain.MemberRepository;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.fixture.ExpenditureFixture;
import com.poolex.poolex.expenditure.fixture.ExpenditureRequestFixture;
import com.poolex.poolex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestMemberTokenGenerator;
import com.poolex.poolex.token.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@DisplayName("지출 인수 테스트")
class ExpenditureControllerTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    private TestMemberTokenGenerator testMemberTokenGenerator;

    @BeforeEach
    void setUp() {
        this.testMemberTokenGenerator = new TestMemberTokenGenerator(memberRepository, jwtTokenProvider);
    }

    @Test
    void 지출을_생성한다() throws Exception {
        //given
        final String accessToken = testMemberTokenGenerator.createTokenWithNewMember("oauthId");
        final ExpenditureCreateRequest request = ExpenditureRequestFixture.getSimpleCreateRequest();

        //when
        //then
        mockMvc.perform(
                post("/expenditures")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_있을_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final LocalDateTime date = LocalDateTime.now();

        createExpenditure(1000, member.getId(), date);
        createExpenditure(2000, member.getId(), date);

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request =
            new MemberWeeklyTotalExpenditureRequest(LocalDate.from(date));

        //when
        //then
        mockMvc.perform(
                get("/expenditures")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(3000));
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_없을_때() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final LocalDateTime date = LocalDateTime.now();

        createExpenditure(1000, member.getId(), date);
        createExpenditure(2000, member.getId(), date);

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request =
            new MemberWeeklyTotalExpenditureRequest(LocalDate.from(date).plusDays(7));

        //when
        //then
        mockMvc.perform(
                get("/expenditures")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(0));
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private void createExpenditure(final int amount, final Long memberId, final LocalDateTime date) {
        expenditureRepository.save(ExpenditureFixture.simpleWith(amount, memberId, date));
    }
}
