package com.poorlex.refactoring.user.member.service.provider;

import com.poorlex.refactoring.user.member.service.dto.BattleDifficultySuccessCountDto;

public interface MemberBattleSuccessCountProvider {

    BattleDifficultySuccessCountDto byMemberId(final Long memberId);
}
