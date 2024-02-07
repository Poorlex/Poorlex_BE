package com.poorlex.poorlex.common;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@MappedSuperclass
public class BaseCreatedAtEntity {

    protected LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
