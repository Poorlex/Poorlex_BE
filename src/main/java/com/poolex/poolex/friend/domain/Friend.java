package com.poolex.poolex.friend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long firstMemberId;
    private Long secondMemberId;

    public Friend(final Long id, final Long firstMemberId, final Long secondMemberId) {
        this.id = id;
        this.firstMemberId = firstMemberId;
        this.secondMemberId = secondMemberId;
    }

    public static Friend withoutId(final Long firstMemberId, final Long secondMemberId) {
        return new Friend(null, firstMemberId, secondMemberId);
    }

    public Long getId() {
        return id;
    }

    public Long getFirstMemberId() {
        return firstMemberId;
    }

    public Long getSecondMemberId() {
        return secondMemberId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Friend friend = (Friend) o;
        return Objects.equals(id, friend.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
