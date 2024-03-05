package com.poorlex.refactoring.user.member.service.provider;

import com.poorlex.refactoring.user.member.service.dto.MemberLevelInfoDto;

public interface MemberLevelInfoProvider {

    MemberLevelInfoDto byMemberId(final Long memberId);
}
