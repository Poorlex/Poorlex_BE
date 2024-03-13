package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureAmount;
import com.poorlex.poorlex.expenditure.domain.ExpenditureDescription;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureImageUnusedEvent;
import com.poorlex.poorlex.expenditure.service.event.ZeroExpenditureCreatedEvent;
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
        final String mainImageUrl = getUploadedImageUrl(mainImage);
        final String subImageUrl = getUploadedImageUrl(subImage);

        return Expenditure.withoutId(amount, memberId, request.getDate(), description, mainImageUrl, subImageUrl);
    }

    private void validateMainImageExist(final MultipartFile mainImage) {
        if (Objects.isNull(mainImage)) {
            throw new IllegalArgumentException("지출 메인 이미지가 없습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 배틀이 존재하지 않습니다."));

        validateUpdateAuthority(memberId, expenditure);
        expenditure.updateAmount(new ExpenditureAmount(request.getAmount()));
        expenditure.updateDescription(new ExpenditureDescription(request.getDescription()));
        updateMainImage(expenditure, updateMainImage, updateMainImageUrl);
        updateSubImage(expenditure, updateSubImage, updateSubImageUrl);
    }

    private void validateUpdateAuthority(final Long memberId, final Expenditure expenditure) {
        if (!expenditure.owned(memberId)) {
            throw new IllegalArgumentException("지출을 수정할 수 있는 권한이 없습니다.");
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

        throw new IllegalArgumentException("지출의 메인 이미지는 반드시 존재해야 합니다.");
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
}
