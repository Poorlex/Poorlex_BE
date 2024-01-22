package com.poorlex.poorlex.expenditure.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.TestMemberTokenGenerator;
import com.poorlex.poorlex.token.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

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
    void 지출을_수정한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());

        final String accessToken = testMemberTokenGenerator.createAccessToken(member);
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000, "updated", List.of("newImageUrl"));

        //when
        //then
        mockMvc.perform(
                patch("/expenditures/{expenditureId}", expenditure.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
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

    @Test
    void Id에_해당하는_지출을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());

        //when
        //then
        mockMvc.perform(get("/expenditures/{expenditureId}", expenditure.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(expenditure.getId()))
            .andExpect(jsonPath("$.date").value(LocalDate.from(expenditure.getDate()).toString()))
            .andExpect(jsonPath("$.amount").value(1000))
            .andExpect(jsonPath("$.description").value(expenditure.getDescription()))
            .andExpect(jsonPath("$.imageUrls.length()").value(expenditure.getImageUrls().getUrls().size()));
    }

    @Test
    void 멤버의_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/expenditures")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(expenditure.getId()))
            .andExpect(jsonPath("$[0].date").value(LocalDate.from(expenditure.getDate()).toString()))
            .andExpect(jsonPath("$[0].amount").value(1000))
            .andExpect(jsonPath("$[0].description").value(expenditure.getDescription()))
            .andExpect(jsonPath("$[0].imageUrls.length()").value(expenditure.getImageUrls().getUrls().size()));
    }

    @Test
    void 배틀의_요일별_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId1");
        final Member other = createMember("oauthId2");
        final Battle battle = createBattle();

        join(member, battle);
        join(other, battle);

        final LocalDateTime battleStart = battle.getDuration().getStart();
        final Expenditure memberExpenditure = createExpenditure(1000, member.getId(), battleStart);
        createExpenditure(1000, member.getId(), battleStart.minusWeeks(1));
        final Expenditure otherExpenditure = createExpenditure(1000, other.getId(), battleStart);
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/expenditures?dayOfWeek={dayOfWeek}",
                    battle.getId(),
                    battleStart.getDayOfWeek().name()
                ).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(memberExpenditure.getId()))
            .andExpect(jsonPath("$[0].imageUrl").value(memberExpenditure.getImageUrls().getUrls().get(0).getValue()))
            .andExpect(jsonPath("$[0].imageCount").value(memberExpenditure.getImageUrls().getUrls().size()))
            .andExpect(jsonPath("$[0].own").value(true))
            .andExpect(jsonPath("$[1].id").value(otherExpenditure.getId()))
            .andExpect(jsonPath("$[1].imageUrl").value(otherExpenditure.getImageUrls().getUrls().get(0).getValue()))
            .andExpect(jsonPath("$[1].imageCount").value(otherExpenditure.getImageUrls().getUrls().size()))
            .andExpect(jsonPath("$[1].own").value(false));
    }

    @Test
    void 멤버의_배틀_지출목록을_조회한다() throws Exception {
        //given
        final Member member = createMember("oauthId1");
        final Member other = createMember("oauthId2");
        final Battle battle = createBattle();

        join(member, battle);
        join(other, battle);

        final LocalDateTime battleStart = battle.getDuration().getStart();
        createExpenditure(3000, member.getId(), battleStart.minusDays(1));
        createExpenditure(2000, other.getId(), battleStart);
        final Expenditure memberExpenditure = createExpenditure(1000, member.getId(), battleStart);

        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        //when
        //then
        mockMvc.perform(
                get("/battles/{battleId}/expenditures", battle.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(memberExpenditure.getId()))
            .andExpect(jsonPath("$[0].imageUrl").value(memberExpenditure.getImageUrls().getUrls().get(0).getValue()))
            .andExpect(jsonPath("$[0].imageCount").value(memberExpenditure.getImageUrls().getUrls().size()))
            .andExpect(jsonPath("$[0].own").value(true));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private void join(final Member member, final Battle battle) {
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private Expenditure createExpenditure(final int amount, final Long memberId, final LocalDateTime date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWith(amount, memberId, date));
    }
}
