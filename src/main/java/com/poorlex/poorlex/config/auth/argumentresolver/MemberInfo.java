package com.poorlex.poorlex.config.auth.argumentresolver;

public class MemberInfo {

    private Long memberId;

    public MemberInfo(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
