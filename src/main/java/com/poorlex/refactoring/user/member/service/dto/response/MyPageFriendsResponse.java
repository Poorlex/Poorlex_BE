package com.poorlex.refactoring.user.member.service.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageFriendsResponse {

    private final int friendTotalCount;
    private final List<MyPageFriendResponse> friends;
}
