package com.poolex.poolex.auth.domain;

import java.util.Objects;

public class MemberId {

    private final Long value;

    public MemberId(final Long value) {
        this.value = value;
    }

    public Long getValue() {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }
}
