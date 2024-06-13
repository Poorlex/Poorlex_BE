package com.poorlex.poorlex.battle.battle.domain;

import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.dto.response.FindingBattleResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.poorlex.poorlex.battle.battle.domain.QBattle.battle;
import static com.poorlex.poorlex.battle.participation.domain.QBattleParticipant.battleParticipant;

@Repository
@RequiredArgsConstructor
public class BattleQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<FindingBattleResponse> queryBattles(BattleFindRequest request, Pageable pageable) {
        return queryFactory.select(Projections.constructor(FindingBattleResponse.class,
                        battle.id,
                        battle.name.value,
                        battle.introduction.value,
                        battle.imageUrl.value,
                        battle.budget.value,
                        battle.maxParticipantSize.value,
                        battleParticipant.count().intValue()
                ))
                .from(battle)
                .leftJoin(battleParticipant).on(battleParticipant.battleId.eq(battle.id))
                .where(buildPredicate(request))
                .groupBy(battle.id)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
    }

    private Predicate buildPredicate(BattleFindRequest request) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            booleanBuilder.and(battle.status.in(request.getStatus()));
        }
        return booleanBuilder;
    }
}
