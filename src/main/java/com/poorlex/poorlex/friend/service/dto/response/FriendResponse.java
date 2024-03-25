package com.poorlex.poorlex.friend.service.dto.response;

import lombok.Getter;

@Getter
public class FriendResponse {

    private final Long id;
    private final int level;
    private final String nickname;
    private final Long weeklyExpenditure;

    public FriendResponse(final Long id, final int level, final String nickname, final Long weeklyExpenditure) {
        this.id = id;
        this.level = level;
        this.nickname = nickname;
        this.weeklyExpenditure = weeklyExpenditure;
    }
}
