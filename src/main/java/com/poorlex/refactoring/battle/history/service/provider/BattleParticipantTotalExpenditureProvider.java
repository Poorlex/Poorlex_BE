package com.poorlex.refactoring.battle.history.service.provider;

import com.poorlex.refactoring.battle.history.service.dto.ParticipantTotalExpenditureDto;
import java.time.LocalDateTime;
import java.util.List;

public interface BattleParticipantTotalExpenditureProvider {

    List<ParticipantTotalExpenditureDto> byParticipantMemberIdAndBetween(final List<Long> memberId,
                                                                         final LocalDateTime start,
                                                                         final LocalDateTime end);
}
