package com.poorlex.poorlex.friend.service.dto.response;

import lombok.Getter;

@Getter
public class FriendResponse {

    private final int level;
    private final String nickname;
    private final int weeklyExpenditure;

    public FriendResponse(final int level, final String nickname, final int weeklyExpenditure) {
        this.level = level;
        this.nickname = nickname;
        this.weeklyExpenditure = weeklyExpenditure;
    }

    @Override
    public String toString() {
        return "FriendResponse{" +
            "level=" + level +
            ", nickname='" + nickname + '\'' +
            ", weeklyExpenditure=" + weeklyExpenditure +
            '}';
    }
}
