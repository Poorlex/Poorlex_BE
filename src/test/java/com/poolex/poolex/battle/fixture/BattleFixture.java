package com.poolex.poolex.battle.fixture;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleBudget;
import com.poolex.poolex.battle.domain.BattleDuration;
import com.poolex.poolex.battle.domain.BattleImageUrl;
import com.poolex.poolex.battle.domain.BattleIntroduction;
import com.poolex.poolex.battle.domain.BattleName;
import com.poolex.poolex.battle.domain.BattleParticipantSize;
import com.poolex.poolex.battle.domain.BattleStatus;

public class BattleFixture {

    private BattleFixture() {
    }

    public static Battle simple() {
        return initialBattleBuilder().build();
    }

    public static Builder initialBattleBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Long id = null;
        private BattleName name = new BattleName("name");
        private BattleIntroduction introduction = new BattleIntroduction("introduction");
        private BattleImageUrl imageUrl = new BattleImageUrl("imageUrl");
        private BattleBudget budget = new BattleBudget(10000);
        private BattleParticipantSize battleParticipantSize = new BattleParticipantSize(10);
        private BattleDuration duration = BattleDuration.current();
        private BattleStatus status = BattleStatus.RECRUITING;

        public Builder name(final BattleName name) {
            this.name = name;
            return this;
        }

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public Builder introduction(final BattleIntroduction introduction) {
            this.introduction = introduction;
            return this;
        }

        public Builder imageUrl(final BattleImageUrl imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder budget(final BattleBudget budget) {
            this.budget = budget;
            return this;
        }

        public Builder battleParticipantSize(final BattleParticipantSize battleParticipantSize) {
            this.battleParticipantSize = battleParticipantSize;
            return this;
        }

        public Builder duration(final BattleDuration duration) {
            this.duration = duration;
            return this;
        }

        public Builder status(final BattleStatus status) {
            this.status = status;
            return this;
        }

        public Battle build() {
            return new Battle(id, name, introduction, imageUrl, budget, battleParticipantSize, duration, status);
        }
    }
}
