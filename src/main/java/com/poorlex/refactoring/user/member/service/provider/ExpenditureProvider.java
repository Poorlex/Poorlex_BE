package com.poorlex.refactoring.user.member.service.provider;

import com.poorlex.refactoring.user.member.service.dto.MyPageExpenditureDto;
import java.util.List;

public interface ExpenditureProvider {

    int countByMemberId(final Long memberId);

    List<MyPageExpenditureDto> byMemberIdLimit(final Long memberId, final int limit);
}
