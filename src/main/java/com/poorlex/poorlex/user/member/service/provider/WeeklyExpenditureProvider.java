package com.poorlex.poorlex.user.member.service.provider;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeeklyExpenditureProvider {

    Long getByMemberId(final Long memberId, final LocalDate date);

    Map<Long, Long> getByMemberIds(final List<Long> memberIds, final LocalDate date);
}
