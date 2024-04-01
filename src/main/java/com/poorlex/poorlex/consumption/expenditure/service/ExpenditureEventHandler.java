package com.poorlex.poorlex.consumption.expenditure.service;

import com.poorlex.poorlex.consumption.expenditure.domain.Expenditure;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.service.event.ExpenditureImageUnusedEvent;
import com.poorlex.poorlex.consumption.expenditure.service.event.ExpenditureMemberDeletedEvent;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ExpenditureEventHandler {

    private final ExpenditureRepository expenditureRepository;
    private final ExpenditureImageService imageService;

    @TransactionalEventListener(classes = {ExpenditureMemberDeletedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final ExpenditureMemberDeletedEvent event) {
        final List<Expenditure> expenditures = expenditureRepository.findAllByMemberId(event.getMemberId());
        removeExpendituresImages(expenditures);
        expenditureRepository.deleteAll(expenditures);
    }

    private void removeExpendituresImages(final List<Expenditure> expenditures) {
        expenditures.forEach(expenditure -> {
            imageService.delete(expenditure.getMainImageUrl());
            expenditure.getSubImageUrl()
                    .ifPresent(imageService::delete);
        });
    }

    @TransactionalEventListener(classes = {ExpenditureImageUnusedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final ExpenditureImageUnusedEvent event) {
        try {
            imageService.delete(event.getImageUrl());
        } catch (Exception e) {
            throw new ApiException(ExceptionTag.AWS_S3, "S3 이미지 삭제에 실패했습니다.");
        }
    }
}
