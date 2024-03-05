package com.poorlex.refactoring.user.member.service.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageExpendituresResponse {

    private final int expenditureTotalCount;
    private final List<MyPageExpenditureResponse> expenditures;
}
