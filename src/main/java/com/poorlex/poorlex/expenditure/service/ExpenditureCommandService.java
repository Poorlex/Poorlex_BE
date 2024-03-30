package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.expenditure.domain.ExpenditureDescription;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.domain.WeeklyExpenditureDuration;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureImageUnusedEvent;
import com.poorlex.poorlex.expenditure.service.event.ZeroExpenditureCreatedEvent;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ExpenditureCommandService {

    private final String bucketDirectory;
    private final ExpenditureRepository expenditureRepository;
    private final AWSS3Service awss3Service;

    public ExpenditureCommandService(@Value("${aws.s3.expenditure-directory}") final String bucketDirectory,
                                     final ExpenditureRepository expenditureRepository,
                                     final AWSS3Service awss3Service) {
        this.expenditureRepository = expenditureRepository;
        this.awss3Service = awss3Service;
        this.bucketDirectory = bucketDirectory;
    }

    public Long createExpenditure(final Long memberId,
                                  final MultipartFile mainImage,
                                  final MultipartFile subImage,
                                  final ExpenditureCreateRequest request) {
        final Expenditure expenditure = generateExpenditure(memberId, request, mainImage, subImage);
        expenditureRepository.save(expenditure);
        raiseEvent(expenditure.getAmount(), memberId);
        return expenditure.getId();
    }

    private Expenditure generateExpenditure(final Long memberId,
                                            final ExpenditureCreateRequest request,
                                            final MultipartFile mainImage,
                                            final MultipartFile subImage) {
        final ExpenditureAmount amount = new ExpenditureAmount(request.getAmount());
        final ExpenditureDescription description = new ExpenditureDescription(request.getDescription());
        validateMainImageExist(mainImage);
        validateDateCreatable(request.getDate());
        final String mainImageUrl = getUploadedImageUrl(mainImage);
        final String subImageUrl = getUploadedImageUrl(subImage);

        return Expenditure.withoutId(amount, memberId, request.getDate(), description, mainImageUrl, subImageUrl);
    }

    private void validateDateCreatable(final LocalDate date) {
        final LocalDate currentDate = LocalDate.now();
        final WeeklyExpenditureDuration weeklyExpenditureDuration = WeeklyExpenditureDuration.from(currentDate);
        if (currentDate.isBefore(date)) {
            final String errorMessage = String.format("현재 날짜 이후의 지출은 생성할 수 없습니다. ( 현재 날짜 : %s , 등록하려는 날짜 : %s )",
                                                      currentDate,
                                                      date);
            throw new ApiException(ExceptionTag.EXPENDITURE_DATE, errorMessage);
        }

        if (date.isBefore(weeklyExpenditureDuration.getStart())) {
            final String errorMessage = String.format("현재 날짜 포함 주 이전의 지출은 생성할 수 없습니다. ( 주 시작 날짜 : %s , 등록하려는 날짜 : %s )",
                                                      weeklyExpenditureDuration.getStart(),
                                                      date);
            throw new ApiException(ExceptionTag.EXPENDITURE_DATE, errorMessage);
        }
    }

    private void validateMainImageExist(final MultipartFile mainImage) {
        if (Objects.isNull(mainImage)) {
            throw new ApiException(ExceptionTag.EXPENDITURE_IMAGE, "지출 메인 이미지가 없습니다.");
        }
    }

    private String getUploadedImageUrl(final MultipartFile image) {
        if (Objects.isNull(image)) {
            return null;
        }
        return awss3Service.uploadMultipartFile(image, bucketDirectory);
    }

    private void raiseEvent(final long expenditureAmount, final Long memberId) {
        if (expenditureAmount == 0) {
            Events.raise(new ZeroExpenditureCreatedEvent(memberId));
            return;
        }
        Events.raise(new ExpenditureCreatedEvent(memberId));
    }

    public void updateExpenditure(final Long expenditureId,
                                  final Long memberId,
                                  final Optional<MultipartFile> updateMainImage,
                                  final Optional<String> updateMainImageUrl,
                                  final Optional<MultipartFile> updateSubImage,
                                  final Optional<String> updateSubImageUrl,
                                  final ExpenditureUpdateRequest request) {
        final Expenditure expenditure = expenditureRepository.findById(expenditureId)
                .orElseThrow(() -> new ApiException(ExceptionTag.EXPENDITURE_UPDATE, "수정하려는 지출이 존재하지 않습니다."));

        validateUpdateAuthority(memberId, expenditure);
        validateUpdatableDate(expenditure);
        expenditure.updateAmount(new ExpenditureAmount(request.getAmount()));
        expenditure.updateDescription(new ExpenditureDescription(request.getDescription()));
        updateMainImage(expenditure, updateMainImage, updateMainImageUrl);
        updateSubImage(expenditure, updateSubImage, updateSubImageUrl);
    }

    private void validateUpdateAuthority(final Long memberId, final Expenditure expenditure) {
        if (!expenditure.owned(memberId)) {
            throw new ApiException(ExceptionTag.EXPENDITURE_UPDATE, "지출을 수정할 수 있는 권한이 없습니다.");
        }
    }

    private void validateUpdatableDate(final Expenditure expenditure) {
        final LocalDate current = LocalDate.now();
        final WeeklyExpenditureDuration currentWeek = WeeklyExpenditureDuration.from(current);
        final LocalDate expenditureDate = expenditure.getDate();
        if (!currentWeek.contains(expenditureDate)) {
            final String errorMessage = String.format("지출은 지출 일자가 포함된 주에만 수정할 수 있습니다. ( 현재 날짜 : %s , 지출 일자 : %s )",
                                                      current,
                                                      expenditureDate.toString());
            throw new ApiException(ExceptionTag.EXPENDITURE_UPDATE, errorMessage);
        }
    }

    private void updateMainImage(final Expenditure expenditure,
                                 final Optional<MultipartFile> mainImage,
                                 final Optional<String> mainImageUrl) {
        if (mainImageUrl.isPresent()) {
            updateMainImageWithImageUrl(expenditure, mainImageUrl);
            return;
        }

        if (mainImage.isPresent()) {
            updateMainImageWithImageFile(expenditure, mainImage);
            return;
        }

        throw new ApiException(ExceptionTag.EXPENDITURE_IMAGE, "지출의 메인 이미지는 반드시 존재해야 합니다.");
    }

    private void updateMainImageWithImageFile(final Expenditure expenditure,
                                              final Optional<MultipartFile> updateMainImage) {
        final String prevMainImageUrl = expenditure.getMainImageUrl();
        updateMainImage.ifPresent(image -> {
            final String updateImageUrl = awss3Service.uploadMultipartFile(image, bucketDirectory);
            expenditure.updateMainImage(updateImageUrl);
        });
        Events.raise(new ExpenditureImageUnusedEvent(prevMainImageUrl));
    }

    private void updateMainImageWithImageUrl(final Expenditure expenditure, final Optional<String> updateMainImageUrl) {
        updateMainImageUrl.ifPresent(mainImageUrl -> {
            if (!expenditure.getMainImageUrl().equals(mainImageUrl)) {
                expenditure.updateMainImage(mainImageUrl);
            }
        });
    }

    private void updateSubImage(final Expenditure expenditure,
                                final Optional<MultipartFile> updateSubImage,
                                final Optional<String> updateSubImageUrl) {
        if (updateSubImageUrl.isPresent()) {
            updateSubImageWithImageUrl(expenditure, updateSubImageUrl);
            return;
        }

        if (updateSubImage.isPresent()) {
            updateSubImageWithImageFile(expenditure, updateSubImage);
            return;
        }

        removeSubImage(expenditure);
    }

    private void updateSubImageWithImageUrl(final Expenditure expenditure, final Optional<String> updateSubImageUrl) {
        updateSubImageUrl.ifPresent(subImageUrl -> {
            if (!expenditure.getSubImageUrl().get().equals(subImageUrl)) {
                expenditure.updateSubImage(subImageUrl);
            }
        });
    }

    private void updateSubImageWithImageFile(final Expenditure expenditure,
                                             final Optional<MultipartFile> updateSubImage) {
        final Optional<String> prevSubImageUrl = expenditure.getSubImageUrl();

        updateSubImage.ifPresent(subImage -> {
            final String updateImageUrl = awss3Service.uploadMultipartFile(subImage, bucketDirectory);
            expenditure.updateSubImage(updateImageUrl);
        });

        prevSubImageUrl.ifPresent(prevImageUrl -> Events.raise(new ExpenditureImageUnusedEvent(prevImageUrl)));
    }

    private void removeSubImage(final Expenditure expenditure) {
        final Optional<String> subImageUrl = expenditure.getSubImageUrl();
        expenditure.removeSubImage();
        subImageUrl.ifPresent(imageUrl -> Events.raise(new ExpenditureImageUnusedEvent(imageUrl)));
    }

    public void deleteExpenditure(final Long memberId, final Long expenditureId) {
        final Expenditure expenditure = expenditureRepository.findById(expenditureId)
                .orElseThrow(() -> new ApiException(ExceptionTag.EXPENDITURE_UPDATE, "삭제하려는 지출이 존재하지 않습니다."));

        if (!expenditure.owned(memberId)) {
            throw new ApiException(ExceptionTag.EXPENDITURE_UPDATE, "삭제하려는 지출이 본인의 지출이 아닙니다.");
        }

        awss3Service.deleteFile(expenditure.getMainImageUrl());
        expenditure.getSubImageUrl().ifPresent(awss3Service::deleteFile);

        expenditureRepository.deleteById(expenditureId);
    }
}
