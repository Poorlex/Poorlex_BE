package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.weeklybudget.domain.WeeklyBudgetDuration;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("지출 서비스 테스트")
class ExpenditureQueryServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private ExpenditureQueryService expenditureQueryService;

    @BeforeEach
    void setUp() {
        this.expenditureQueryService = new ExpenditureQueryService(battleRepository, expenditureRepository);
    }

    @Test
    @Transactional
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_있을_때() {
        //given
        final Member member = createMember("oauthId");
        final LocalDate date = LocalDate.now();
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(date);

        createExpenditureWithMainImage(1000L, member.getId(), LocalDate.from(weeklyExpenditureDuration.getStart()));
        createExpenditureWithMainImage(2000L, member.getId(), LocalDate.from(weeklyExpenditureDuration.getStart()));

        //when
        final MemberWeeklyTotalExpenditureResponse response =
                expenditureQueryService.findMemberWeeklyTotalExpenditure(member.getId(), date);

        //then
        assertThat(response.getAmount()).isEqualTo(3000L);
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_없을_때() {
        //given
        final WeeklyBudgetDuration weeklyBudgetDuration = WeeklyBudgetDuration.current();
        final Member member = createMember("oauthId");

        createExpenditureWithMainImage(1000L,
                                       member.getId(),
                                       LocalDate.from(weeklyBudgetDuration.getStart()).minusDays(1));
        createExpenditureWithMainImage(2000L,
                                       member.getId(),
                                       LocalDate.from(weeklyBudgetDuration.getStart()).minusDays(1));

        //when
        final MemberWeeklyTotalExpenditureResponse response =
                expenditureQueryService.findMemberWeeklyTotalExpenditure(member.getId(),
                                                                         weeklyBudgetDuration.getStart());

        //then
        assertThat(response.getAmount()).isZero();
    }

    @Test
    @Transactional
    void 해당_ID를_가진_지출을_조회한다() {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), LocalDate.now());

        //when
        final ExpenditureResponse response = expenditureQueryService.findExpenditureById(expenditure.getId());

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                    softly.assertThat(response.getDate()).isEqualTo(LocalDate.from(expenditure.getDate()));
                    softly.assertThat(response.getAmount()).isEqualTo(expenditure.getAmount());
                    softly.assertThat(response.getDescription()).isEqualTo(expenditure.getDescription());
                    softly.assertThat(response.getMainImageUrl()).isNotEmpty();
                }
        );
    }

    @Test
    @Transactional
    void 멤버의_지출목록을_조회한다() {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), LocalDate.now());

        //when
        final List<ExpenditureResponse> responses = expenditureQueryService.findMemberExpenditures(member.getId());

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(responses).hasSize(1);

                    final ExpenditureResponse response = responses.get(0);
                    softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                    softly.assertThat(response.getDate()).isEqualTo(LocalDate.from(expenditure.getDate()));
                    softly.assertThat(response.getAmount()).isEqualTo(expenditure.getAmount());
                    softly.assertThat(response.getDescription()).isEqualTo(expenditure.getDescription());
                    softly.assertThat(response.getMainImageUrl()).isNotEmpty();
                    softly.assertThat(response.getSubImageUrl()).isNull();
                }
        );
    }

    @Test
    @Transactional
    void 멤버의_배틀_지출목록을_조회한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        join(battle, member);

        final LocalDate battleStartDate = LocalDate.from(battle.getDuration().getStart());
        createExpenditureWithMainImage(1000L, member.getId(), battleStartDate.minusDays(1));
        final Expenditure expenditure = createExpenditureWithMainImage(1000L, member.getId(), battleStartDate);

        //when
        final List<BattleExpenditureResponse> responses =
                expenditureQueryService.findMemberBattleExpenditures(battle.getId(), member.getId());

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(responses).hasSize(1);

                    final BattleExpenditureResponse response = responses.get(0);
                    softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                    softly.assertThat(response.getImageCount()).isOne();
                    softly.assertThat(response.getImageUrl()).isEqualTo(expenditure.getMainImageUrl());
                    softly.assertThat(response.isOwn()).isTrue();
                }
        );
    }

    @Test
    @Transactional
    void 멤버가_포함된_배틀의_요일별_지출목록을_조회한다_지출이_있을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        final Member other = createMember("oauthId2");

        join(battle, member);
        join(battle, other);

        final LocalDate battleStart = LocalDate.from(battle.getDuration().getStart());
        final Expenditure memberExpenditure = createExpenditureWithMainImage(1000L, member.getId(), battleStart);
        createExpenditureWithMainImage(2000L, member.getId(), battleStart.minusWeeks(1));
        final Expenditure otherExpenditure = createExpenditureWithMainImage(1000L, other.getId(), battleStart);

        //when
        final List<BattleExpenditureResponse> responses = expenditureQueryService.findBattleExpendituresInDayOfWeek(
                battle.getId(),
                member.getId(),
                memberExpenditure.getDate().getDayOfWeek().name()
        );

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(responses).hasSize(2);

                    final BattleExpenditureResponse firstResponse = responses.get(0);
                    softly.assertThat(firstResponse.getId()).isEqualTo(memberExpenditure.getId());
                    softly.assertThat(firstResponse.getImageUrl()).isEqualTo(memberExpenditure.getMainImageUrl());
                    softly.assertThat(firstResponse.getImageCount()).isEqualTo(memberExpenditure.getImageCounts());
                    softly.assertThat(firstResponse.isOwn()).isTrue();

                    final BattleExpenditureResponse secondResponse = responses.get(1);
                    softly.assertThat(secondResponse.getId()).isEqualTo(otherExpenditure.getId());
                    softly.assertThat(secondResponse.getImageUrl()).isEqualTo(otherExpenditure.getMainImageUrl());
                    softly.assertThat(secondResponse.getImageCount()).isEqualTo(otherExpenditure.getImageCounts());
                    softly.assertThat(secondResponse.isOwn()).isFalse();
                }
        );
    }

    @Test
    void 멤버가_포함된_배틀의_요일별_지출목록을_조회한다_지출이_없을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        final Member other = createMember("oauthId2");

        join(battle, member);
        join(battle, other);

        final Expenditure memberExpenditure = createExpenditureWithMainImage(1000L,
                                                                             member.getId(),
                                                                             LocalDate.from(battle.getDuration()
                                                                                                    .getStart()));
        createExpenditureWithMainImage(1000L, other.getId(), LocalDate.from(battle.getDuration().getStart()));

        //when
        final List<BattleExpenditureResponse> responses = expenditureQueryService.findBattleExpendituresInDayOfWeek(
                battle.getId(),
                member.getId(),
                memberExpenditure.getDate().getDayOfWeek().plus(1).name()
        );

        //then
        assertThat(responses).isEmpty();
    }

//    @Test
//    void 지출을_수정한다() {
//        //given
//        final Member member = createMember("oauthId");
//        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());
//        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000, "updated", List.of("newImageUrl"));
//
//        //when
//        expenditureService.updateExpenditure(member.getId(), expenditure.getId(), request);
//
//        //then
//        final Expenditure updateExpenditure = expenditureRepository.findById(expenditure.getId())
//            .orElseThrow(IllegalArgumentException::new);
//        final List<String> updateExpenditureImageUrls = updateExpenditure.getImageUrls().getUrls().stream()
//            .map(ExpenditureCertificationImageUrl::getValue).toList();
//
//        assertThat(updateExpenditure.getId()).isEqualTo(expenditure.getId());
//        assertThat(updateExpenditure.getMemberId()).isEqualTo(member.getId());
//        assertThat(updateExpenditure.getAmount()).isEqualTo(request.getAmount());
//        assertThat(updateExpenditure.getDescription()).isEqualTo(request.getDescription());
//        assertThat(updateExpenditureImageUrls).isEqualTo(request.getImageUrls());
//        assertThat(updateExpenditure.getDateTime()).isEqualTo(expenditure.getDateTime());
//    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname")));
    }

    private Expenditure createExpenditureWithMainImage(final Long amount, final Long memberId, final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, memberId, date));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private void join(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }
}
