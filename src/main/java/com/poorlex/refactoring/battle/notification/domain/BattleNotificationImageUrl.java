package com.poorlex.refactoring.battle.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleNotificationImageUrl {

    @Column(name = "image_url", columnDefinition = "text")
    private String value;

    public BattleNotificationImageUrl(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}
