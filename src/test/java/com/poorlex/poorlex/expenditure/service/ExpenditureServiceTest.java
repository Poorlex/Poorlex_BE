package com.poorlex.poorlex.expenditure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.BattleExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.ExpenditureResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("지출 서비스 테스트")
class ExpenditureServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    private ExpenditureService expenditureService;

    @BeforeEach
    void setUp() {
        this.expenditureService = new ExpenditureService(battleRepository, expenditureRepository);
    }

    @Test
    void 지출을_생성한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final ExpenditureCreateRequest createRequest = ExpenditureRequestFixture.getSimpleCreateRequest();

        //when
        final Long createdExpenditureId = expenditureService.createExpenditure(member.getId(), createRequest);

        //then
        final Expenditure expenditure = expenditureRepository.findById(createdExpenditureId)
            .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
            softly -> {
                softly.assertThat(expenditure.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(expenditure.getAmount()).isEqualTo(createRequest.getAmount());
                softly.assertThat(expenditure.getDescription()).isEqualTo(createRequest.getDescription());
                softly.assertThat(expenditure.getDateTime()).isNotNull();
                softly.assertThat(expenditure.getImageUrls().getUrls())
                    .usingRecursiveComparison().ignoringFields("id")
                    .isEqualTo(List.of(
                        ExpenditureCertificationImageUrl.withoutId(createRequest.getImageUrls().get(0), expenditure),
                        ExpenditureCertificationImageUrl.withoutId(createRequest.getImageUrls().get(1), expenditure)
                    ));
            }
        );
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_있을_때() {
        //given
        final Member member = createMember("oauthId");
        final LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(date);

        createExpenditure(1000, member.getId(), weeklyExpenditureDuration.getStart());
        createExpenditure(2000, member.getId(), weeklyExpenditureDuration.getStart());

        final MemberWeeklyTotalExpenditureRequest request =
            new MemberWeeklyTotalExpenditureRequest(LocalDateTime.from(date));

        //when
        final MemberWeeklyTotalExpenditureResponse response = expenditureService.findMemberWeeklyTotalExpenditure(
            member.getId(),
            request
        );

        //then
        assertThat(response.getAmount()).isEqualTo(3000);
    }

    @Test
    void 멤버의_기간중의_지출의_총합을_구한다_지출이_없을_때() {
        //given
        final Member member = createMember("oauthId");
        final LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        createExpenditure(1000, member.getId(), date);
        createExpenditure(2000, member.getId(), date);

        final MemberWeeklyTotalExpenditureRequest request =
            new MemberWeeklyTotalExpenditureRequest(LocalDateTime.from(date).plusDays(7));

        //when
        final MemberWeeklyTotalExpenditureResponse response = expenditureService.findMemberWeeklyTotalExpenditure(
            member.getId(),
            request
        );

        //then
        assertThat(response.getAmount()).isZero();
    }

    @Test
    void 해당_ID를_가진_지출을_조회한다() {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());

        //when
        final ExpenditureResponse response = expenditureService.findExpenditureById(expenditure.getId());

        //then
        final List<String> expectedImageUrls = expenditure.getImageUrls().getUrls()
            .stream()
            .map(ExpenditureCertificationImageUrl::getValue)
            .toList();

        assertSoftly(
            softly -> {
                softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                softly.assertThat(response.getDate()).isEqualTo(LocalDate.from(expenditure.getDateTime()));
                softly.assertThat(response.getAmount()).isEqualTo(expenditure.getAmount());
                softly.assertThat(response.getDescription()).isEqualTo(expenditure.getDescription());
                softly.assertThat(response.getImageUrls()).containsExactlyElementsOf(expectedImageUrls);
            }
        );
    }

    @Test
    void 멤버의_지출목록을_조회한다() {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());

        //when
        final List<ExpenditureResponse> responses = expenditureService.findMemberExpenditures(member.getId());

        //then
        final List<String> expectedImageUrls = expenditure.getImageUrls().getUrls()
            .stream()
            .map(ExpenditureCertificationImageUrl::getValue)
            .toList();

        assertSoftly(
            softly -> {
                softly.assertThat(responses).hasSize(1);

                final ExpenditureResponse response = responses.get(0);
                softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                softly.assertThat(response.getDate()).isEqualTo(LocalDate.from(expenditure.getDateTime()));
                softly.assertThat(response.getAmount()).isEqualTo(expenditure.getAmount());
                softly.assertThat(response.getDescription()).isEqualTo(expenditure.getDescription());
                softly.assertThat(response.getImageUrls()).containsExactlyElementsOf(expectedImageUrls);
            }
        );
    }

    @Test
    void 멤버의_배틀_지출목록을_조회한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        join(battle, member);

        final LocalDateTime battleStart = battle.getDuration().getStart();
        createExpenditure(1000, member.getId(), battleStart.minusDays(1));
        final Expenditure expenditure = createExpenditure(1000, member.getId(), battleStart);

        //when
        final List<BattleExpenditureResponse> responses =
            expenditureService.findMemberBattleExpenditures(battle.getId(), member.getId());

        //then
        final List<ExpenditureCertificationImageUrl> imageUrls = expenditure.getImageUrls().getUrls();

        assertSoftly(
            softly -> {
                softly.assertThat(responses).hasSize(1);

                final BattleExpenditureResponse response = responses.get(0);
                softly.assertThat(response.getId()).isEqualTo(expenditure.getId());
                softly.assertThat(response.getImageUrl()).isEqualTo(imageUrls.get(0).getValue());
                softly.assertThat(response.getImageCount()).isEqualTo(imageUrls.size());
                softly.assertThat(response.isOwn()).isTrue();
            }
        );
    }

    @Test
    void 멤버가_포함된_배틀의_요일별_지출목록을_조회한다_지출이_있을_때() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        final Member other = createMember("oauthId2");

        join(battle, member);
        join(battle, other);

        final LocalDateTime battleStart = battle.getDuration().getStart();
        final Expenditure memberExpenditure = createExpenditure(1000, member.getId(), battleStart);
        createExpenditure(2000, member.getId(), battleStart.minusWeeks(1));
        final Expenditure otherExpenditure = createExpenditure(1000, other.getId(), battleStart);

        //when
        final List<BattleExpenditureResponse> responses = expenditureService.findBattleExpendituresInDayOfWeek(
            battle.getId(),
            member.getId(),
            memberExpenditure.getDateTime().getDayOfWeek().name()
        );

        //then
        final List<ExpenditureCertificationImageUrl> memberExpenditureImageUrls =
            memberExpenditure.getImageUrls().getUrls();
        final List<ExpenditureCertificationImageUrl> otherExpenditureImageUrls =
            otherExpenditure.getImageUrls().getUrls();

        assertSoftly(
            softly -> {
                softly.assertThat(responses).hasSize(2);

                final BattleExpenditureResponse firstResponse = responses.get(0);
                softly.assertThat(firstResponse.getId()).isEqualTo(memberExpenditure.getId());
                softly.assertThat(firstResponse.getImageUrl()).isEqualTo(memberExpenditureImageUrls.get(0).getValue());
                softly.assertThat(firstResponse.getImageCount()).isEqualTo(memberExpenditureImageUrls.size());
                softly.assertThat(firstResponse.isOwn()).isTrue();

                final BattleExpenditureResponse secondResponse = responses.get(1);
                softly.assertThat(secondResponse.getId()).isEqualTo(otherExpenditure.getId());
                softly.assertThat(secondResponse.getImageUrl()).isEqualTo(otherExpenditureImageUrls.get(0).getValue());
                softly.assertThat(secondResponse.getImageCount()).isEqualTo(otherExpenditureImageUrls.size());
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

        final Expenditure memberExpenditure = createExpenditure(1000, member.getId(), battle.getDuration().getStart());
        createExpenditure(1000, other.getId(), battle.getDuration().getStart());

        //when
        final List<BattleExpenditureResponse> responses = expenditureService.findBattleExpendituresInDayOfWeek(
            battle.getId(),
            member.getId(),
            memberExpenditure.getDateTime().getDayOfWeek().plus(1).name()
        );

        //then
        assertThat(responses).isEmpty();
    }

    @Test
    void 지출을_수정한다() {
        //given
        final Member member = createMember("oauthId");
        final Expenditure expenditure = createExpenditure(1000, member.getId(), LocalDateTime.now());
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000, "updated", List.of("newImageUrl"));

        //when
        expenditureService.updateExpenditure(member.getId(), expenditure.getId(), request);

        //then
        final Expenditure updateExpenditure = expenditureRepository.findById(expenditure.getId())
            .orElseThrow(IllegalArgumentException::new);
        final List<String> updateExpenditureImageUrls = updateExpenditure.getImageUrls().getUrls().stream()
            .map(ExpenditureCertificationImageUrl::getValue).toList();

        assertThat(updateExpenditure.getId()).isEqualTo(expenditure.getId());
        assertThat(updateExpenditure.getMemberId()).isEqualTo(member.getId());
        assertThat(updateExpenditure.getAmount()).isEqualTo(request.getAmount());
        assertThat(updateExpenditure.getDescription()).isEqualTo(request.getDescription());
        assertThat(updateExpenditureImageUrls).isEqualTo(request.getImageUrls());
        assertThat(updateExpenditure.getDateTime()).isEqualTo(expenditure.getDateTime());
    }

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private Expenditure createExpenditure(final int amount, final Long memberId, final LocalDateTime date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWith(amount, memberId, date));
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(BattleStatus.PROGRESS).build());
    }

    private void join(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }
}
