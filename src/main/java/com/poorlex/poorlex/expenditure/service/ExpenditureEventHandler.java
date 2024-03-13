package com.poorlex.poorlex.expenditure.service;

import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.member.service.event.MemberDeletedEvent;
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
    private final AWSS3Service awss3Service;

    @TransactionalEventListener(classes = {MemberDeletedEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(final MemberDeletedEvent event) {
        final List<Expenditure> expenditures = expenditureRepository.findAllByMemberId(event.getMemberId());
        removeExpendituresImages(expenditures);
        expenditureRepository.deleteAll(expenditures);
    }

    private void removeExpendituresImages(final List<Expenditure> expenditures) {
        expenditures.forEach(expenditure -> {
            awss3Service.deleteFile(expenditure.getMainImageUrl());
            expenditure.getSubImageUrl()
                    .ifPresent(awss3Service::deleteFile);
        });
    }
}
