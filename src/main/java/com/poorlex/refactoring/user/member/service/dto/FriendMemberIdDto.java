package com.poorlex.refactoring.user.member.service.dto;

public class FriendMemberIdDto {

    private final Long memberId;

    public FriendMemberIdDto(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
