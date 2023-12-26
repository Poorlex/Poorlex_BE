package com.poolex.poolex.expenditure.service;

import com.poolex.poolex.expenditure.domain.Expenditure;
import com.poolex.poolex.expenditure.domain.ExpenditureRepository;
import com.poolex.poolex.expenditure.service.dto.ExpenditureCreateRequest;
import com.poolex.poolex.expenditure.service.mapper.ExpenditureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;

    public Long createExpenditure(final Long memberId, final ExpenditureCreateRequest request) {
        final Expenditure expenditure = ExpenditureMapper.createRequestToExpenditure(memberId, request);
        final Expenditure savedExpenditure = expenditureRepository.save(expenditure);

        return savedExpenditure.getId();
    }
}
