package com.poolex.poolex.config.auth.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class RequestMemberInfo {

    private Long memberId;

    public void setMemberId(final Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
