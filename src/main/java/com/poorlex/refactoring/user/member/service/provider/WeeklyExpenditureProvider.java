package com.poorlex.refactoring.user.member.service.provider;

import java.time.LocalDate;

public interface WeeklyExpenditureProvider {

    Long byMemberIdContains(final Long memberId, final LocalDate date);
}
