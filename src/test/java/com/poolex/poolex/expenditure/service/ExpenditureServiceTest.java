package com.poolex.poolex.expenditure.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.fixture.ExpenditureRequestFixture;
import com.poolex.poolex.expenditure.service.dto.ExpenditureCreateRequest;
import com.poolex.poolex.login.domain.Member;
import com.poolex.poolex.login.domain.MemberNickname;
import com.poolex.poolex.login.domain.MemberRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.TestDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("지출 서비스 테스트")
class ExpenditureServiceTest extends TestDataJpaTest implements ReplaceUnderScoreTest {

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
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
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
}
