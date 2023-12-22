package com.poolex.poolex.battle.service;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.domain.BattleStatus;
import com.poolex.poolex.battle.domain.BattleWithCurrentParticipantSize;
import com.poolex.poolex.battle.service.dto.request.BattleCreateRequest;
import com.poolex.poolex.battle.service.dto.response.FindingBattleResponse;
import com.poolex.poolex.battle.service.event.BattleCreatedEvent;
import com.poolex.poolex.battle.service.mapper.BattleMapper;
import com.poolex.poolex.config.event.Events;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleService {

    private final BattleRepository battleRepository;

    @Transactional
    public Long create(final Long memberId, final BattleCreateRequest request) {
        final Battle battle = BattleMapper.createRequestToBattle(request);
        battleRepository.save(battle);
        Events.raise(new BattleCreatedEvent(battle.getId(), memberId));
        return battle.getId();
    }

    public List<FindingBattleResponse> findBattlesToPlay() {
        final List<BattleStatus> statuses = List.of(BattleStatus.RECRUITING, BattleStatus.RECRUITING_FINISHED);

        final List<BattleWithCurrentParticipantSize> battlesByMemberIdWithCurrentParticipantSize =
            battleRepository.findBattlesByStatusesWithCurrentParticipantSizeIn(statuses);

        return FindingBattleResponse.parseToList(battlesByMemberIdWithCurrentParticipantSize);
    }
}
