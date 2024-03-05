package com.poorlex.refactoring.battle.alarm.domain;

import com.poorlex.refactoring.common.BaseCreatedAtEntity;
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
public class BattleAlarm extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long battleId;

    private Long memberId;

    @Enumerated(value = EnumType.STRING)
    private BattleAlarmType type;

    public BattleAlarm(final Long id,
                       final Long battleId,
                       final Long memberId,
                       final BattleAlarmType type) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.type = type;
    }

    public static BattleAlarm withoutId(final Long battleId,
                                        final Long memberId,
                                        final BattleAlarmType type) {
        return new BattleAlarm(null, battleId, memberId, type);
    }

    public boolean isReactable() {
        return this.type != BattleAlarmType.EXPENDITURE_NEEDED
            && this.type != BattleAlarmType.BATTLE_NOTIFICATION_CHANGED;
    }

    public Long getId() {
        return id;
    }

    public Long getBattleId() {
        return battleId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public BattleAlarmType getType() {
        return type;
    }
}
