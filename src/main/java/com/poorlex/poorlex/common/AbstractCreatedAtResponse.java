package com.poorlex.poorlex.common;

import java.time.LocalDateTime;

public abstract class AbstractCreatedAtResponse {

    private final LocalDateTime createdAt;

    protected AbstractCreatedAtResponse(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
