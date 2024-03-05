package com.poorlex.refactoring.battle.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleName {

    @Column(name = "name", nullable = false)
    private String value;

    BattleName(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
