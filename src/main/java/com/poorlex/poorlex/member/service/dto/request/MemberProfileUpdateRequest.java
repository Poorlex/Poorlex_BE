package com.poorlex.poorlex.member.service.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberProfileUpdateRequest {

    private final String nickname;
    private final String description;
}
