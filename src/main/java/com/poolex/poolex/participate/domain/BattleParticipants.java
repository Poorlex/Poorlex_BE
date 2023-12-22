package com.poolex.poolex.participate.domain;

import java.util.List;

public class BattleParticipants {

    private final List<BattleParticipant> participants;
    private final int maxSize;

    public BattleParticipants(final List<BattleParticipant> participants, final int maxSize) {
        validateSize(participants, maxSize);
        this.maxSize = maxSize;
        this.participants = participants;
    }

    private void validateSize(final List<BattleParticipant> participants, final int maxSize) {
        if (participants.size() > maxSize) {
            throw new IllegalArgumentException();
        }
    }

    public void addParticipant(final BattleParticipant newParticipant) {
        validateNotFull();
        validateParticipantUnique(newParticipant);
        participants.add(newParticipant);
    }

    private void validateNotFull() {
        if (participants.size() >= maxSize) {
            throw new IllegalArgumentException();
        }
    }

    private void validateParticipantUnique(final BattleParticipant participant) {
        if (participants.contains(participant)) {
            throw new IllegalArgumentException();
        }
    }
}
