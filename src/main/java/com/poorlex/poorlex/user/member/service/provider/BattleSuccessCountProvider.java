package com.poorlex.poorlex.user.member.service.provider;

import com.poorlex.poorlex.battle.service.dto.response.BattleSuccessCountResponse;

public interface BattleSuccessCountProvider {

    BattleSuccessCountResponse getByMemberId(final Long memberId);
}
