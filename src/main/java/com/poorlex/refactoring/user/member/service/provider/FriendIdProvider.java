package com.poorlex.refactoring.user.member.service.provider;

import com.poorlex.refactoring.user.member.service.dto.FriendMemberIdDto;
import java.util.List;

public interface FriendIdProvider {

    List<FriendMemberIdDto> byMemberIdLimit(final Long memberId, final int limit);
}
