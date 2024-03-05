package com.poorlex.refactoring.user.member.service.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageExpenditureDto {

    private final Long id;
    private final LocalDate date;
    private final long amount;
    private final String imageUrl;
}
