package com.poorlex.refactoring.user.member.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberLevelInfoDto {

    private final int level;
    private final int point;
    private final Integer pointLeftForLevelUp;
}
