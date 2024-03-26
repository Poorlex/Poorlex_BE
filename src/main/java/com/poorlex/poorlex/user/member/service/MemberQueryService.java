package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.battlesuccess.service.BattleSuccessService;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.expenditure.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.friend.service.FriendService;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.point.service.MemberPointService;
import com.poorlex.poorlex.point.service.dto.response.MyPageLevelInfoResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberIdAndNicknameDto;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberPointService memberPointService;
    private final FriendService friendService;
    private final BattleSuccessService battleSuccessService;
    private final ExpenditureQueryService expenditureQueryService;

    public Map<Long, String> getMembersNickname(final List<Long> memberIds) {
        return memberRepository.getMemberNicknamesByMemberIds(memberIds)
                .stream()
                .collect(Collectors.toMap(MemberIdAndNicknameDto::getMemberId, MemberIdAndNicknameDto::getNickname));
    }

    public MyPageResponse getMyPageInfoFromCurrentDatetime(final Long memberId) {
        return getMyPageInfo(memberId, LocalDate.now());
    }

    public MyPageResponse getMyPageInfo(final Long memberId, final LocalDate date) {
        final Member member = getMemberById(memberId);
        final MyPageLevelInfoResponse memberLevelInfo = memberPointService.findMemberLevelInfo(memberId);
        final BattleSuccessCountResponse battleSuccessCounts =
                battleSuccessService.findMemberBattleSuccessCounts(memberId);
        final List<FriendResponse> friends = friendService.findMemberFriends(memberId, date);
        final List<MyPageExpenditureResponse> expenditures = expenditureQueryService.findMemberExpenditures(memberId)
                .stream()
                .map(MyPageExpenditureResponse::from)
                .toList();

        return new MyPageResponse(
                member.getNickname(),
                member.getDescription().orElse(null),
                memberLevelInfo,
                battleSuccessCounts,
                friends,
                expenditures
        );
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
    }
}
