package com.poorlex.refactoring.user.member.service.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageFriendResponse {

    private final Long memberId;
    private final int level;
    private final String nickname;
    private final Long weeklyExpenditure;
}
