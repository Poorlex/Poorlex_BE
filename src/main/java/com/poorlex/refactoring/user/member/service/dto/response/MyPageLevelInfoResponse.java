package com.poorlex.refactoring.user.member.service.dto.response;

import com.poorlex.refactoring.user.member.service.dto.MemberLevelInfoDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageLevelInfoResponse {

    private final int level;
    private final int point;
    private final Integer pointLeftForLevelUp;

    public static MyPageLevelInfoResponse from(final MemberLevelInfoDto memberLevelInfoDto) {
        return new MyPageLevelInfoResponse(
            memberLevelInfoDto.getLevel(),
            memberLevelInfoDto.getPoint(),
            memberLevelInfoDto.getPointLeftForLevelUp()
        );
    }
}
