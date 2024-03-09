package com.poorlex.poorlex.battle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleImageUrl {

    @Column(name = "image_url", columnDefinition = "text", nullable = false)
    private String value;

    public BattleImageUrl(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("배틀 이미지 URL 이 비어있습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
