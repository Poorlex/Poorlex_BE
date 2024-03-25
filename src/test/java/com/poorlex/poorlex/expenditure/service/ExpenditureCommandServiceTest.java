package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import jakarta.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("지출 서비스 테스트")
class ExpenditureCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private AWSS3Service awss3Service;

    private ExpenditureCommandService expenditureCommandService;

    @BeforeEach
    void setUp() {
        this.expenditureCommandService = new ExpenditureCommandService("bucketDirectory",
                                                                       expenditureRepository,
                                                                       awss3Service);
    }

    @Test
    @Transactional
    void 지출을_생성한다() throws IOException {
        //given
        final String mainImageUploadedUrl = "uploadedImageUrl";
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn(mainImageUploadedUrl);
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final ExpenditureCreateRequest request = ExpenditureRequestFixture.getSimpleCreateRequest();

        //when
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final Long createdExpenditureId = expenditureCommandService.createExpenditure(member.getId(),
                                                                                      image,
                                                                                      null,
                                                                                      request);

        //then
        final Expenditure expenditure = expenditureRepository.findById(createdExpenditureId)
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(expenditure.getMemberId()).isEqualTo(member.getId());
                    softly.assertThat(expenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(expenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(expenditure.getDate()).isNotNull();
                    softly.assertThat(expenditure.getMainImageUrl()).isEqualTo(mainImageUploadedUrl);
                    softly.assertThat(expenditure.getSubImageUrl()).isEmpty();
                }
        );
    }

    @Test
    void 지출을_수정한다_이미지가_변경되지_않은_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getMainImageUrl()),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getSubImageUrl().get()),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevMainImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isPresent().get().isEqualTo(prevSubImageUrl);
                }
        );
    }

    @Test
    void 지출을_수정한다_모든_이미지가_새로운_이미지로_수정되는_경우() throws IOException {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final String newMainImageUrl = "newMainImage";
        final String newSubImageUrl = "newSubImage";
        given(awss3Service.uploadMultipartFile(eq(mainImage), any())).willReturn(newMainImageUrl);
        given(awss3Service.uploadMultipartFile(eq(subImage), any())).willReturn(newSubImageUrl);

        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.of(mainImage),
                                                    Optional.empty(),
                                                    Optional.of(subImage),
                                                    Optional.empty(),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(newMainImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isPresent().get().isEqualTo(newSubImageUrl);
                }
        );
    }

    @Test
    void 지출을_수정한다_서브_이미지만_새로운_이미지로_수정되는_경우() throws IOException {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevExpenditureImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final String newSubImageUrl = "newSubImage";
        given(awss3Service.uploadMultipartFile(eq(subImage), any())).willReturn(newSubImageUrl);

        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getMainImageUrl()),
                                                    Optional.of(subImage),
                                                    Optional.empty(),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevExpenditureImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isPresent().get().isEqualTo(newSubImageUrl);
                }
        );
    }

    @Test
    void 지출을_수정한다_메인_이미지만_새로운_이미지로_수정되는_경우() throws IOException {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final String newMainImageUrl = "newMainImage";
        given(awss3Service.uploadMultipartFile(eq(mainImage), any())).willReturn(newMainImageUrl);

        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.of(mainImage),
                                                    Optional.empty(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getSubImageUrl().get()),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(newMainImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isPresent().get().isEqualTo(prevSubImageUrl);
                }
        );
    }

    @Test
    void 지출을_수정한다_메인_이미지가_서브_이미지_URL로_수정되는_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getSubImageUrl().get()),
                                                    Optional.empty(),
                                                    Optional.empty(),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevSubImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isEmpty();
                }
        );
    }

    @Test
    void 지출을_수정한다_서브_이미지가_메인_이미지_URL로_수정되는_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getSubImageUrl().get()),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getMainImageUrl()),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevSubImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl())
                            .isPresent()
                            .get()
                            .isEqualTo(prevMainImageUrl);
                }
        );
    }

    @Test
    void 지출을_수정한다_서브_이미지가_삭제된_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000,
                                                                                  member.getId(),
                                                                                  LocalDate.now());
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(2000L, "업데이트된 소개");

        //when
        expenditureCommandService.updateExpenditure(expenditure.getId(),
                                                    member.getId(),
                                                    Optional.empty(),
                                                    Optional.of(expenditure.getMainImageUrl()),
                                                    Optional.empty(),
                                                    Optional.empty(),
                                                    request);

        entityManager.flush();
        entityManager.clear();

        //then
        final Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId())
                .orElseThrow(IllegalArgumentException::new);

        assertSoftly(
                softly -> {
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevMainImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isEmpty();
                }
        );
    }

    private Expenditure createExpenditureWithMainImageAndSubImage(final int amount,
                                                                  final Long memberId,
                                                                  final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImageAndSubImage(amount, memberId, date));
    }
}
