package com.poorlex.poorlex.friend.service;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.consumption.expenditure.service.ExpenditureQueryService;
import com.poorlex.poorlex.consumption.expenditure.service.dto.response.MemberWeeklyTotalExpenditureResponse;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.friend.service.dto.request.FriendCreateRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendDenyRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendInviteRequest;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.friend.service.event.FriendAcceptedEvent;
import com.poorlex.poorlex.friend.service.event.FriendDeniedEvent;
import com.poorlex.poorlex.friend.service.event.FriendInvitedEvent;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final ExpenditureQueryService expenditureQueryService;
    private final MemberPointRepository memberPointRepository;

    @Transactional
    public void inviteFriend(final Long memberId, final FriendInviteRequest request) {
        final FriendInvitedEvent friendInvitedEvent = FriendInvitedEvent.builder()
                .inviteMemberId(memberId)
                .invitedMemberId(request.getInviteMemberId())
                .build();

        Events.raise(friendInvitedEvent);
    }

    @Transactional
    public void inviteDeny(final Long memberId, final FriendDenyRequest request) {
        final FriendDeniedEvent friendDeniedEvent = FriendDeniedEvent.builder()
                .inviteMemberId(request.getInviteMemberId())
                .denyMemberId(memberId)
                .build();

        Events.raise(friendDeniedEvent);
    }

    @Transactional
    public void inviteAccept(final Long memberId, final FriendCreateRequest request) {
        final Long friendMemberId = request.getFriendMemberId();
        validateFriendExist(memberId, friendMemberId);
        friendRepository.save(Friend.withoutId(memberId, friendMemberId));

        Events.raise(
                FriendAcceptedEvent.builder()
                        .inviteMemberId(friendMemberId)
                        .acceptMemberId(memberId)
                        .build()
        );
    }

    private void validateFriendExist(final Long memberId, final Long friendId) {
        final boolean firstExistCheck = friendRepository.existsByFirstMemberIdAndSecondMemberId(memberId, friendId);
        final boolean secondExistCheck = friendRepository.existsByFirstMemberIdAndSecondMemberId(friendId, memberId);
        if (firstExistCheck || secondExistCheck) {
            throw new IllegalArgumentException("이미 존재하는 친구입니다.");
        }
    }

    public List<FriendResponse> findMemberFriendsWithCurrentDateTime(final Long memberId) {
        return findMemberFriends(memberId, LocalDate.now());
    }

    public List<FriendResponse> findMemberFriends(final Long memberId, final LocalDate date) {
        final List<Long> friendMemberIds = friendRepository.findFriendIdsByMemberId(memberId);

        return friendMemberIds.stream()
                .map(friendMemberId -> generateResponse(friendMemberId, date))
                .toList();
    }

    private FriendResponse generateResponse(final Long friendMemberId, final LocalDate date) {
        return new FriendResponse(friendMemberId,
                                  getFriendLevel(friendMemberId),
                                  getFriendNickname(friendMemberId),
                                  getFriendWeeklyTotalExpenditure(friendMemberId, date));
    }

    private Long getFriendWeeklyTotalExpenditure(final Long friendMemberId, final LocalDate date) {
        final MemberWeeklyTotalExpenditureResponse weeklyTotalExpenditure =
                expenditureQueryService.findMemberWeeklyTotalExpenditure(friendMemberId, date);

        return weeklyTotalExpenditure.getAmount();
    }

    private String getFriendNickname(final Long friendMemberId) {
        return memberRepository.findMemberNicknameByMemberId(friendMemberId);
    }

    private int getFriendLevel(final Long friendMemberId) {
        final int totalPoint = memberPointRepository.findSumByMemberId(friendMemberId);

        return MemberLevel.findByPoint(new Point(totalPoint))
                .orElseThrow(IllegalArgumentException::new)
                .getNumber();
    }
}
