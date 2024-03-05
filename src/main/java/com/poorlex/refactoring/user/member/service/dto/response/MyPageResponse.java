package com.poorlex.refactoring.user.member.service.dto.response;

import lombok.Getter;

@Getter
public class MyPageResponse {

    private final String nickname;
    private final String description;
    private final MyPageLevelInfoResponse levelInfo;
    private final BattleSuccessCountResponse battleSuccessInfo;
    private final MyPageFriendsResponse friends;
    private final MyPageExpendituresResponse expenditures;

    public MyPageResponse(final String nickname,
                          final String description,
                          final MyPageLevelInfoResponse levelInfo,
                          final BattleSuccessCountResponse battleSuccessInfo,
                          final MyPageFriendsResponse friends,
                          final MyPageExpendituresResponse expenditures) {
        this.nickname = nickname;
        this.description = description;
        this.levelInfo = levelInfo;
        this.battleSuccessInfo = battleSuccessInfo;
        this.friends = friends;
        this.expenditures = expenditures;
    }
}
