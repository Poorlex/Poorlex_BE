package com.poorlex.refactoring.battle.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleImageUrl {

    @Column(name = "image_url", columnDefinition = "text", nullable = false)
    private String value;

    BattleImageUrl(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
