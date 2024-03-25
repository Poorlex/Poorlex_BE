package com.poorlex.poorlex.user.member.service.provider;

import java.util.List;

public interface FriendProvider {

    List<Long> getByMemberId(final Long memberId);
}
