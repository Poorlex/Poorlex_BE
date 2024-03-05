package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureRepository;
import com.poorlex.refactoring.user.member.service.dto.MyPageExpenditureDto;
import com.poorlex.refactoring.user.member.service.provider.ExpenditureProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenditureProviderImpl implements ExpenditureProvider {

    private final ExpenditureRepository expenditureRepository;

    @Override
    public int countByMemberId(final Long memberId) {
        return expenditureRepository.countByMemberId(memberId);
    }

    @Override
    public List<MyPageExpenditureDto> byMemberIdLimit(final Long memberId, final int limit) {
        final List<Expenditure> expenditures =
            expenditureRepository.findExpenditureByMemberId(memberId, PageRequest.of(0, limit));

        return expenditures.stream()
            .map(
                expenditure -> new MyPageExpenditureDto(
                    expenditure.getId(),
                    expenditure.getDate(),
                    expenditure.getAmount(),
                    expenditure.getImageUrls().getUrls().get(0).getValue()
                ))
            .toList();
    }
}
