package com.poorlex.refactoring.battle.battle.service;

import com.poorlex.refactoring.battle.battle.domain.Battle;
import com.poorlex.refactoring.battle.battle.domain.BattleRepository;
import com.poorlex.refactoring.battle.battle.service.dto.request.BattleCreateRequest;
import com.poorlex.refactoring.battle.battle.service.mapper.BattleMapper;
import com.poorlex.refactoring.battle.battle.service.validation.BattleServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleCommandService {

    private final BattleRepository battleRepository;
    private final BattleServiceValidator validator;

    public Long create(final Long memberId, final BattleCreateRequest request) {
        validator.memberParticipateBattleUnderMaxCount(memberId);
        final Battle battle = battleRepository.save(BattleMapper.createRequestToBattle(request));
        return battle.getId();
    }
}
