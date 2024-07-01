package com.poorlex.poorlex.consumption.expenditure.service;

import com.poorlex.poorlex.consumption.expenditure.domain.Expenditure;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.consumption.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.consumption.expenditure.fixture.ExpenditureRequestFixture;
import com.poorlex.poorlex.consumption.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.consumption.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.BadRequestException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import jakarta.persistence.EntityManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

@DisplayName("지출 서비스 테스트")
class ExpenditureCommandServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private ExpenditureImageService imageService;

    private ExpenditureCommandService expenditureCommandService;

    @BeforeEach
    void setUp() {
        this.expenditureCommandService = new ExpenditureCommandService("bucketDirectory",
                                                                       expenditureRepository,
                                                                       imageService);
        initializeDataBase();
    }

    @Test
    void 지출을_생성한다() throws IOException {
        //given
        final String mainImageUploadedUrl = "uploadedImageUrl";
        given(imageService.saveAndReturnPath(any(), any())).willReturn(mainImageUploadedUrl);
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
    void ERROR_미래의_지출을_생성시_예외를_던진다() throws IOException {
        //given
        final String mainImageUploadedUrl = "uploadedImageUrl";
        given(imageService.saveAndReturnPath(any(), any())).willReturn(mainImageUploadedUrl);
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final LocalDate currentDate = LocalDate.now();
        final ExpenditureCreateRequest request = ExpenditureRequestFixture.getWithDate(currentDate.plusDays(2));

        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        final String expectedErrorMessage = String.format("현재 날짜 이후의 지출은 생성할 수 없습니다. ( 현재 날짜 : %s , 등록하려는 날짜 : %s )",
                                                          currentDate,
                                                          request.getDate());

        assertThatThrownBy(() -> expenditureCommandService.createExpenditure(member.getId(),
                                                                             image,
                                                                             null,
                                                                             request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.EXPENDITURE_DATE)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    void ERROR_이전_주의_지출_생성시_예외를_던진다() throws IOException {
        //given
        final String mainImageUploadedUrl = "uploadedImageUrl";
        given(imageService.saveAndReturnPath(any(), any())).willReturn(mainImageUploadedUrl);
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final LocalDate currentDate = LocalDate.now();
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(currentDate);
        final ExpenditureCreateRequest request = ExpenditureRequestFixture.getWithDate(weeklyExpenditureDuration.getStart()
                                                                                               .minusDays(1));

        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        //then
        final String expectedErrorMessage = String.format(
                "현재 날짜 포함 주 이전의 지출은 생성할 수 없습니다. ( 주 시작 날짜 : %s , 등록하려는 날짜 : %s )",
                weeklyExpenditureDuration.getStart(),
                request.getDate());

        assertThatThrownBy(() -> expenditureCommandService.createExpenditure(member.getId(),
                                                                             image,
                                                                             null,
                                                                             request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.EXPENDITURE_DATE)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    void 지출을_수정한다_이미지가_변경되지_않은_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        final LocalDate currentDate = LocalDate.now();
        final WeeklyExpenditureDuration weeklyDuration = WeeklyExpenditureDuration.from(currentDate);
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  weeklyDuration.getStart());
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(currentDate, 2000L, "업데이트된 소개");

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(currentDate);
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                    member.getId(),
                                                                                    now);
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

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
        given(imageService.saveAndReturnPath(eq(mainImage), any())).willReturn(newMainImageUrl);
        given(imageService.saveAndReturnPath(eq(subImage), any())).willReturn(newSubImageUrl);

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  now);
        final String prevExpenditureImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

        //when
        final MockMultipartFile subImage = new MockMultipartFile(
                "subImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final String newSubImageUrl = "newSubImage";
        given(imageService.saveAndReturnPath(eq(subImage), any())).willReturn(newSubImageUrl);

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  now);
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

        //when
        final MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        final String newMainImageUrl = "newMainImage";
        given(imageService.saveAndReturnPath(eq(mainImage), any())).willReturn(newMainImageUrl);

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  now);
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  now);
        final String prevSubImageUrl = expenditure.getSubImageUrl().get();
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
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
        LocalDate now = LocalDate.now();
        final Expenditure expenditure = createExpenditureWithMainImageAndSubImage(1000L,
                                                                                  member.getId(),
                                                                                  now);
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

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
                    softly.assertThat(updatedExpenditure.getDate()).isEqualTo(request.getDate());
                    softly.assertThat(updatedExpenditure.getAmount()).isEqualTo(request.getAmount());
                    softly.assertThat(updatedExpenditure.getDescription()).isEqualTo(request.getDescription());
                    softly.assertThat(updatedExpenditure.getMainImageUrl()).isEqualTo(prevMainImageUrl);
                    softly.assertThat(updatedExpenditure.getSubImageUrl()).isEmpty();
                }
        );
    }

    @Test
    void ERROR_다른_회원의_지출을_수정하려는_경우() {
        //given
        final Member 스플릿 = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId1", new MemberNickname("스플릿")));
        final Member 푸얼렉스 = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId2", new MemberNickname("푸얼렉스")));
        LocalDate now = LocalDate.now();
        final Expenditure 푸얼렉스_지출 = createExpenditureWithMainImageAndSubImage(1000L,
                                                                              푸얼렉스.getId(),
                                                                              now);
        final ExpenditureUpdateRequest 지출_수정_요청 = new ExpenditureUpdateRequest(now, 2000L, "업데이트된 소개");

        //when
        //then
        assertThatThrownBy(() -> expenditureCommandService.updateExpenditure(푸얼렉스_지출.getId(),
                                                                             스플릿.getId(),
                                                                             Optional.empty(),
                                                                             Optional.of(푸얼렉스_지출.getSubImageUrl()
                                                                                                 .get()),
                                                                             Optional.empty(),
                                                                             Optional.of(푸얼렉스_지출.getMainImageUrl()),
                                                                             지출_수정_요청))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.EXPENDITURE_UPDATE)
                .hasMessage("지출을 수정할 수 있는 권한이 없습니다.");
    }

    @Test
    void ERROR_현재_날짜가_포함된_주에_등록하지_않은_지출을_수정하려는_경우() {
        //given
        final Member 스플릿 = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId1", new MemberNickname("스플릿")));
        final LocalDate currentDate = LocalDate.now();
        final WeeklyExpenditureDuration weeklyDuration = WeeklyExpenditureDuration.from(currentDate);

        LocalDate date = weeklyDuration.getStart().minusDays(1);
        final Expenditure 스플릿_지출 = createExpenditureWithMainImageAndSubImage(1000L,
                                                                               스플릿.getId(),
                                                                               date);
        final ExpenditureUpdateRequest 지출_수정_요청 = new ExpenditureUpdateRequest(date, 2000L, "업데이트된 소개");

        //when
        //then
        final String expectedErrorMessage = String.format("지출은 지출 일자가 포함된 주에만 수정할 수 있습니다. ( 현재 날짜 : %s , 지출 일자 : %s )",
                                                          LocalDate.now(),
                                                          스플릿_지출.getDate().toString());

        assertThatThrownBy(() -> expenditureCommandService.updateExpenditure(스플릿_지출.getId(),
                                                                             스플릿.getId(),
                                                                             Optional.empty(),
                                                                             Optional.of(스플릿_지출.getSubImageUrl()
                                                                                                 .get()),
                                                                             Optional.empty(),
                                                                             Optional.of(스플릿_지출.getMainImageUrl()),
                                                                             지출_수정_요청))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.EXPENDITURE_UPDATE)
                .hasMessage(expectedErrorMessage);
    }

    @Test
    void ERROR_변경하려는_날짜에_이미_지출이_존재하는_경우() {
        //given
        final Member member = memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId1", new MemberNickname("member1")));
        final LocalDate currentDate = LocalDate.now();
        final WeeklyExpenditureDuration weeklyDuration = WeeklyExpenditureDuration.from(currentDate);

        LocalDate date1 = weeklyDuration.getStart().plusDays(1);
        LocalDate date2 = weeklyDuration.getStart().plusDays(2);
        final Expenditure expenditure1 = createExpenditureWithMainImageAndSubImage(1000L,
                member.getId(),
                date1);
        final Expenditure expenditure2 = createExpenditureWithMainImageAndSubImage(1000L,
                member.getId(),
                date2);
        final ExpenditureUpdateRequest request = new ExpenditureUpdateRequest(date2, 2000L, "업데이트된 소개");

        //when
        //then
        final String expectedErrorMessage = "변경하려는 날짜에 이미 지출이 있습니다.";

        assertThatThrownBy(() -> expenditureCommandService.updateExpenditure(expenditure1.getId(),
                                                                            member.getId(),
                                                                            Optional.empty(),
                                                                            Optional.of(expenditure1.getMainImageUrl()),
                                                                            Optional.empty(),
                                                                            Optional.empty(),
                                                                            request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("tag", ExceptionTag.EXPENDITURE_UPDATE)
                .hasMessage(expectedErrorMessage);
    }

    private Expenditure createExpenditureWithMainImageAndSubImage(final Long amount,
                                                                  final Long memberId,
                                                                  final LocalDate date) {
        return expenditureRepository.save(ExpenditureFixture.simpleWithMainImageAndSubImage(amount, memberId, date));
    }
}
