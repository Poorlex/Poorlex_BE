package com.poorlex.poorlex.point.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberPointResponse {

    private final int totalPoint;
    private final int level;
}
