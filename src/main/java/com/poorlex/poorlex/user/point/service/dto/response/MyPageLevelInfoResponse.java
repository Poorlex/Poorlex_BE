package com.poorlex.poorlex.user.point.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageLevelInfoResponse {

    private final int level;
    private final int point;
    private final Integer pointLeftForLevelUp;
}
