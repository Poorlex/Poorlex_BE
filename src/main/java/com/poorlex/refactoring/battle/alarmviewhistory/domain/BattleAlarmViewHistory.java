package com.poorlex.refactoring.battle.alarmviewhistory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleAlarmViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long battleId;
    private Long memberId;
    private LocalDateTime lastViewTime;

    public BattleAlarmViewHistory(final Long id,
                                  final Long battleId,
                                  final Long memberId,
                                  final LocalDateTime lastViewTime) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.lastViewTime = lastViewTime;
    }

    public static BattleAlarmViewHistory withoutId(final Long battleId,
                                                   final Long memberId,
                                                   final LocalDateTime lastViewTime) {
        return new BattleAlarmViewHistory(null, battleId, memberId, lastViewTime);
    }

    public void updateLastViewTime(final LocalDateTime viewTime) {
        if (lastViewTime.isAfter(viewTime)) {
            return;
        }
        this.lastViewTime = viewTime;
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

    public LocalDateTime getLastViewTime() {
        return lastViewTime;
    }
}
