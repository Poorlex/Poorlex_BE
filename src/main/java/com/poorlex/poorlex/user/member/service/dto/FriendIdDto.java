package com.poorlex.poorlex.user.member.service.dto;

import lombok.Getter;

@Getter
public class FriendIdDto {

    private final Long id;

    public FriendIdDto(final Long id) {
        this.id = id;
    }
}
