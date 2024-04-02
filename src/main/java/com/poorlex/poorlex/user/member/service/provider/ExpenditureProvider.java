package com.poorlex.poorlex.user.member.service.provider;

import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ExpenditureProvider {

    List<ExpenditureDto> getByMemberIdPageable(final Long memberId, final Pageable pageable);

    Long getAllExpenditureCountByMemberId(final Long memberId);
}
