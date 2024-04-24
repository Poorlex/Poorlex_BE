package com.poorlex.poorlex.battle.succession.domain;

import com.poorlex.poorlex.battle.battle.domain.BattleDifficulty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleSuccessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long battleId;
    @Enumerated(value = EnumType.STRING)
    private BattleDifficulty battleDifficulty;

    public BattleSuccessHistory(final Long id,
                                final Long memberId,
                                final Long battleId,
                                final BattleDifficulty battleDifficulty) {
        this.id = id;
        this.memberId = memberId;
        this.battleId = battleId;
        this.battleDifficulty = battleDifficulty;
    }

    public static BattleSuccessHistory withoutId(final Long memberId,
                                                 final Long battleId,
                                                 final BattleDifficulty battleDifficulty) {
        return new BattleSuccessHistory(null, memberId, battleId, battleDifficulty);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getBattleId() {
        return battleId;
    }

    public BattleDifficulty getBattleDifficulty() {
        return battleDifficulty;
    }
}
