package com.poorlex.poorlex.friend.service;

import com.poorlex.poorlex.config.event.Events;
import com.poorlex.poorlex.expenditure.service.ExpenditureService;
import com.poorlex.poorlex.expenditure.service.dto.request.MemberWeeklyTotalExpenditureRequest;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.friend.service.dto.request.FriendCreateRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendDenyRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendInviteRequest;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.friend.service.event.FriendAcceptedEvent;
import com.poorlex.poorlex.friend.service.event.FriendDeniedEvent;
import com.poorlex.poorlex.friend.service.event.FriendInvitedEvent;
import com.poorlex.poorlex.member.domain.MemberLevel;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.point.domain.MemberPointRepository;
import com.poorlex.poorlex.point.domain.Point;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final ExpenditureService expenditureService;
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
        return findMemberFriends(memberId, LocalDateTime.now());
    }

    public List<FriendResponse> findMemberFriends(final Long memberId, final LocalDateTime dateTime) {
        final List<Long> friendMemberIds = friendRepository.findMembersFriendMemberId(memberId);
        final LocalDateTime currentDateTime = dateTime.truncatedTo(ChronoUnit.MICROS);

        return friendMemberIds.stream()
            .map(friendMemberId -> generateResponse(friendMemberId, currentDateTime))
            .toList();
    }

    private FriendResponse generateResponse(final Long friendMemberId, final LocalDateTime dateTime) {
        return new FriendResponse(
            getFriendLevel(friendMemberId),
            getFriendNickname(friendMemberId),
            getFriendWeeklyTotalExpenditure(friendMemberId, dateTime)
        );
    }

    private int getFriendWeeklyTotalExpenditure(final Long friendMemberId, final LocalDateTime datetime) {
        return expenditureService.findMemberWeeklyTotalExpenditure(
            friendMemberId,
            new MemberWeeklyTotalExpenditureRequest(datetime)
        ).getAmount();
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
