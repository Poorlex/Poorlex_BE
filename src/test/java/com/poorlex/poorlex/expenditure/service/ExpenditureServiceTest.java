package com.poorlex.poorlex.expenditure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
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

    private ExpenditureService expenditureService;

    @BeforeEach
    void setUp() {
        this.expenditureService = new ExpenditureService(expenditureRepository);
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
                softly.assertThat(expenditure.getDate()).isNotNull();
                softly.assertThat(expenditure.getImageUrls().getImageUrls())
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

        createExpenditure(1000, member.getId(), date);
        createExpenditure(2000, member.getId(), date);

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

    private Member createMember(final String oauthId) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname("nickname")));
    }

    private void createExpenditure(final int amount, final Long memberId, final LocalDateTime date) {
        expenditureRepository.save(ExpenditureFixture.simpleWith(amount, memberId, date));
    }
}
