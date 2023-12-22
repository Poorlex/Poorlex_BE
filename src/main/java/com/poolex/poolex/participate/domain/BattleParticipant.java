package com.poolex.poolex.participate.domain;

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
public class BattleParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long battleId;
    private Long memberId;
    @Enumerated(value = EnumType.STRING)
    private BattleParticipantRole role;

    private BattleParticipant(final Long id,
                              final Long battleId,
                              final Long memberId,
                              final BattleParticipantRole role) {
        this.id = id;
        this.battleId = battleId;
        this.memberId = memberId;
        this.role = role;
    }

    public static BattleParticipant manager(final Long battleId, final Long memberId) {
        return new BattleParticipant(null,
            battleId,
            memberId,
            BattleParticipantRole.MANAGER
        );
    }

    public static BattleParticipant normalPlayer(final Long battleId, final Long memberId) {
        return new BattleParticipant(null, battleId, memberId, BattleParticipantRole.NORMAL_PLAYER);
    }

    public boolean isManager() {
        return BattleParticipantRole.MANAGER == this.role;
    }

    public boolean isNormalPlayer() {
        return BattleParticipantRole.NORMAL_PLAYER == this.role;
    }
    
    public boolean hasSameBattleId(final Long targetBattleId) {
        return this.battleId.equals(targetBattleId);
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

    public BattleParticipantRole getRole() {
        return role;
    }
}
