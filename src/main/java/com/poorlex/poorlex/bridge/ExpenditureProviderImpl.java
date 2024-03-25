package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
import com.poorlex.poorlex.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import com.poorlex.poorlex.user.member.service.provider.ExpenditureProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenditureProviderImpl implements ExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public List<ExpenditureDto> getByMemberId(final Long memberId) {
        final List<Expenditure> expenditures = expenditureRepository.findAllByMemberId(memberId);
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
}
