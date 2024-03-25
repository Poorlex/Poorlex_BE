package com.poorlex.poorlex.user.member.service.provider;

import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import java.util.List;

public interface ExpenditureProvider {

    List<ExpenditureDto> getByMemberId(final Long memberId);
}
