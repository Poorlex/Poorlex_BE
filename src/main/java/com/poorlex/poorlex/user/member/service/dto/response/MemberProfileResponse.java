package com.poorlex.poorlex.user.member.service.dto.response;

import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.point.service.dto.response.MyPageLevelInfoResponse;

public record MemberProfileResponse(String nickname, String description, MyPageLevelInfoResponse levelInfo) {

    public static MemberProfileResponse of(Member member, MyPageLevelInfoResponse levelInfo) {
        return new MemberProfileResponse(member.getNickname(), member.getDescription().orElse(null), levelInfo);
    }
}
