package com.poolex.poolex.point.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberLevelBarResponse {

    private final int levelRange;
    private final int currentPoint;
    private final int recentPoint;
}
