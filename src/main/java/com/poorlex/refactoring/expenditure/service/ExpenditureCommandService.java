package com.poorlex.refactoring.expenditure.service;

import com.poorlex.refactoring.config.event.Events;
import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureCertificationImageUrl;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.refactoring.expenditure.service.dto.request.ExpenditureUpdateRequest;
import com.poorlex.refactoring.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.refactoring.expenditure.service.mapper.ExpenditureMapper;
import com.poorlex.refactoring.expenditure.service.validate.ExpenditureValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ExpenditureCommandService {

    private final String bucketDirectory;
    private final ImageUploader imageUploader;
    private final ExpenditureValidator validator;
    private final ExpenditureRepository expenditureRepository;

    public ExpenditureCommandService(final ImageUploader imageUploader,
                                     final ExpenditureValidator expenditureValidator,
                                     final ExpenditureRepository expenditureRepository,
                                     @Value("${aws.s3.expenditure-directory}") final String uploadPath
    ) {
        this.validator = expenditureValidator;
        this.expenditureRepository = expenditureRepository;
        this.imageUploader = imageUploader;
        this.bucketDirectory = uploadPath;
    }

    public Long createExpenditure(final Long memberId,
                                  final List<MultipartFile> images,
                                  final ExpenditureCreateRequest request) {
        final Expenditure expenditure = ExpenditureMapper.mappedBy(memberId, request);
        expenditureRepository.save(expenditure);
        saveAndAddImages(expenditure, images);
        Events.raise(new ExpenditureCreatedEvent(memberId));
        return expenditure.getId();
    }

    private void saveAndAddImages(final Expenditure expenditure, final List<MultipartFile> images) {
        validator.imageExist(images);
        images.stream()
            .map(image -> imageUploader.uploadAndReturnPath(image, bucketDirectory))
            .map(imageUrl -> ExpenditureCertificationImageUrl.withoutId(imageUrl, expenditure))
            .forEach(expenditure::addImageUrl);
    }

    public void updateExpenditure(final Long editorId,
                                  final Long expenditureId,
                                  final ExpenditureUpdateRequest request) {
        final Expenditure expenditure = expenditureRepository.findById(expenditureId)
            .orElseThrow(() -> new IllegalArgumentException("Id에 해당하는 지출이 없습니다."));
        validator.isOwner(expenditure, editorId);
//        expenditure.pasteAmountAndDescriptionAndImageUrls(
//            ExpenditureMapper.mappedBy(editorId, request)
//        );
    }
}
