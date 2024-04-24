package com.poorlex.poorlex.bridge;

import com.poorlex.poorlex.battle.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.battle.succession.domain.BattleSuccessCountGroup;
import com.poorlex.poorlex.battle.succession.domain.BattleSuccessHistoryRepository;
import com.poorlex.poorlex.user.member.service.provider.BattleSuccessCountProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleSuccessCountProviderImpl implements BattleSuccessCountProvider {

    private final BattleSuccessHistoryRepository battleSuccessHistoryRepository;

    @Override
    public BattleSuccessCountResponse getByMemberId(final Long memberId) {
        final List<BattleSuccessCountGroup> successCountGroups =
                battleSuccessHistoryRepository.findDifficultySuccessCountsByMemberId(memberId);

        return new BattleSuccessCountResponse(successCountGroups);
    }
}
