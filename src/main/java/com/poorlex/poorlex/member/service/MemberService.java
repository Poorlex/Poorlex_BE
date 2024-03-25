package com.poorlex.poorlex.member.service;

import com.poorlex.poorlex.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.battlesuccess.service.BattleSuccessService;
import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.expenditure.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.friend.service.FriendService;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberDescription;
import com.poorlex.poorlex.member.domain.MemberIdAndNicknameDto;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.service.dto.request.MemberProfileUpdateRequest;
import com.poorlex.poorlex.member.service.dto.response.MyPageResponse;
import com.poorlex.poorlex.member.service.event.MemberDeletedEvent;
import com.poorlex.poorlex.point.service.MemberPointService;
import com.poorlex.poorlex.point.service.dto.response.MyPageLevelInfoResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

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

    @Transactional
    public void updateProfile(final Long memberId, final MemberProfileUpdateRequest request) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
        final String newNickname = request.getNickname();
        final String newDescription = request.getDescription();

        if (Objects.nonNull(newNickname)) {
            member.changeNickname(new MemberNickname(request.getNickname()));
        }
        if (Objects.nonNull(newDescription)) {
            member.changeDescription(new MemberDescription(request.getDescription()));
        }
    }

    public MyPageResponse getMyPageInfoFromCurrentDatetime(final Long memberId) {
        return getMyPageInfo(memberId, LocalDate.now());
    }

    public MyPageResponse getMyPageInfo(final Long memberId, final LocalDate date) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
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

    @Transactional
    public void deleteMember(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
        memberRepository.delete(member);

        Events.raise(new MemberDeletedEvent(memberId));
    }
}
