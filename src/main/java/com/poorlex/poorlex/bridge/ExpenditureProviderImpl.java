package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.consumption.expenditure.domain.Expenditure;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import com.poorlex.poorlex.user.member.service.provider.ExpenditureProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenditureProviderImpl implements ExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public List<ExpenditureDto> getByMemberIdPageable(final Long memberId, final Pageable pageable) {
        final List<Expenditure> expenditures = expenditureRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId,
                                                                                                           pageable);
        return expenditures.stream()
                .map(this::mapToExpenditureDto)
                .toList();
    }

    private ExpenditureDto mapToExpenditureDto(final Expenditure expenditure) {
        return new ExpenditureDto(expenditure.getId(),
                                  expenditure.getDate(),
                                  expenditure.getAmount(),
                                  expenditure.getMainImageUrl());
    }

    @Override
    public Long getAllExpenditureCountByMemberId(final Long memberId) {
        return expenditureRepository.countAllByMemberId(memberId);
    }
}
