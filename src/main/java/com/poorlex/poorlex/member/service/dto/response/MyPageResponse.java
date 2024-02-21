package com.poorlex.poorlex.member.service.dto.response;

import com.poorlex.poorlex.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.expenditure.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.point.service.dto.response.MyPageLevelInfoResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {

    private final String nickname;
    private final String description;
    private final MyPageLevelInfoResponse levelInfo;
    private final BattleSuccessCountResponse battleSuccessCounts;
    private final int friendTotalCount;
    private final List<FriendResponse> friends;
    private final int expenditureTotalCount;
    private final List<MyPageExpenditureResponse> expenditures;

    public MyPageResponse(final String nickname,
                          final String description,
                          final MyPageLevelInfoResponse levelInfo,
                          final BattleSuccessCountResponse battleSuccessCounts,
                          final List<FriendResponse> friends,
                          final List<MyPageExpenditureResponse> expenditures) {
        this.nickname = nickname;
        this.description = description;
        this.levelInfo = levelInfo;
        this.battleSuccessCounts = battleSuccessCounts;
        this.friendTotalCount = friends.size();
        this.friends = friends;
        this.expenditureTotalCount = expenditures.size();
        this.expenditures = expenditures;
    }
}
