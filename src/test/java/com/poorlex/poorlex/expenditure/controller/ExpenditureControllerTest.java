package com.poorlex.poorlex.expenditure.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(dateTime);

        createExpenditure(1000, member.getId(), weeklyExpenditureDuration.getStart());
        createExpenditure(2000, member.getId(), weeklyExpenditureDuration.getStart());

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request = new MemberWeeklyTotalExpenditureRequest(dateTime);

        //when
        //then
        mockMvc.perform(
                get("/expenditures/weekly")
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
        final LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        createExpenditure(1000, member.getId(), dateTime);
        createExpenditure(2000, member.getId(), dateTime);

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final MemberWeeklyTotalExpenditureRequest request =
            new MemberWeeklyTotalExpenditureRequest(dateTime.plusDays(7));

        //when
        //then
        mockMvc.perform(
                get("/expenditures/weekly")
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
