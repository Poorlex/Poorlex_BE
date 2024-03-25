package com.poorlex.poorlex.user.point.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberPointAndLevelResponse {

    private final int totalPoint;
    private final int level;
}
