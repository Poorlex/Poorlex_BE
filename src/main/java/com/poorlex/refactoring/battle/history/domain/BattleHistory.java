package com.poorlex.refactoring.battle.history.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private String battleName;
    private String battleImageUrl;
    private Long memberBudgetLeft;
    private int memberBattleRank;
    private int battleMaxParticipantSize;
    private int memberEarnedPoint;
    private LocalDate battleEnd;
    @Enumerated(value = EnumType.STRING)
    private BattleDifficulty battleDifficulty;
    private boolean successed;

    public BattleHistory(final Long id,
                         final Long memberId,
                         final String battleName,
                         final String battleImageUrl,
                         final Long memberBudgetLeft,
                         final int memberBattleRank,
                         final int battleMaxParticipantSize,
                         final int memberEarnedPoint,
                         final LocalDate battleEnd,
                         final BattleDifficulty battleDifficulty,
                         final boolean successed) {
        this.id = id;
        this.memberId = memberId;
        this.battleName = battleName;
        this.battleImageUrl = battleImageUrl;
        this.memberBudgetLeft = memberBudgetLeft;
        this.memberBattleRank = memberBattleRank;
        this.battleMaxParticipantSize = battleMaxParticipantSize;
        this.memberEarnedPoint = memberEarnedPoint;
        this.battleEnd = battleEnd;
        this.battleDifficulty = battleDifficulty;
        this.successed = successed;
    }

    public static BattleHistory withoutId(final Long memberId,
                                          final String battleName,
                                          final String battleImageUrl,
                                          final Long memberBudgetLeft,
                                          final int memberBattleRank,
                                          final int battleMaxParticipantSize,
                                          final int memberEarnedPoint,
                                          final LocalDate battleEnd,
                                          final BattleDifficulty battleDifficulty,
                                          final boolean isSuccession) {
        return new BattleHistory(null,
            memberId,
            battleName,
            battleImageUrl,
            memberBudgetLeft,
            memberBattleRank,
            battleMaxParticipantSize,
            memberEarnedPoint,
            battleEnd,
            battleDifficulty,
            isSuccession
        );
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getBattleName() {
        return battleName;
    }

    public String getBattleImageUrl() {
        return battleImageUrl;
    }

    public Long getMemberBudgetLeft() {
        return memberBudgetLeft;
    }

    public int getMemberBattleRank() {
        return memberBattleRank;
    }

    public int getBattleMaxParticipantSize() {
        return battleMaxParticipantSize;
    }

    public int getMemberEarnedPoint() {
        return memberEarnedPoint;
    }

    public LocalDate getBattleEnd() {
        return battleEnd;
    }

    public BattleDifficulty getBattleDifficulty() {
        return battleDifficulty;
    }

}
