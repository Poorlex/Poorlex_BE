package com.poorlex.poorlex.consumption.weeklybudget.service.provider;

import java.time.LocalDate;

public interface TotalExpenditureProvider {

    Long byMemberIdBetween(final Long memberId, final LocalDate start, final LocalDate end);
}
