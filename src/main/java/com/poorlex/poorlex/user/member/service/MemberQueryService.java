package com.poorlex.poorlex.user.member.service;

import com.poorlex.poorlex.battle.battle.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberIdAndNicknameDto;
import com.poorlex.poorlex.user.member.domain.MemberLevel;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.service.dto.ExpenditureDto;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.poorlex.user.member.service.dto.response.MyPageResponse;
import com.poorlex.poorlex.user.member.service.provider.BattleSuccessCountProvider;
import com.poorlex.poorlex.user.member.service.provider.ExpenditureProvider;
import com.poorlex.poorlex.user.member.service.provider.FriendProvider;
import com.poorlex.poorlex.user.member.service.provider.WeeklyExpenditureProvider;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import com.poorlex.poorlex.user.point.service.dto.response.MyPageLevelInfoResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private static final PageRequest EXPENDITURE_PAGEABLE = PageRequest.of(0, 4);
    private final MemberRepository memberRepository;
    private final MemberPointRepository memberPointRepository;
    private final BattleSuccessCountProvider battleSuccessCountProvider;
    private final FriendProvider friendProvider;
    private final WeeklyExpenditureProvider weeklyExpenditureProvider;
    private final ExpenditureProvider expenditureProvider;

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
        final MyPageLevelInfoResponse levelInfo = getLevelInfo(memberId);
        final BattleSuccessCountResponse battleSuccessCounts = battleSuccessCountProvider.getByMemberId(memberId);
        final List<FriendResponse> friends = getFriends(memberId, date);
        final List<MyPageExpenditureResponse> expenditures = getExpenditure(memberId);
        final Long expendituresCount = expenditureProvider.getAllExpenditureCountByMemberId(memberId);

        return new MyPageResponse(
                member.getNickname(),
                member.getDescription().orElse(null),
                levelInfo,
                battleSuccessCounts,
                friends,
                expendituresCount,
                expenditures
        );
    }

    private MyPageLevelInfoResponse getLevelInfo(final Long memberId) {
        final int sumPoint = memberPointRepository.findSumByMemberId(memberId);
        final MemberLevel memberLevel = getMemberLevelBySumPoint(sumPoint);
        final Integer pointForNextLevel = memberLevel.getGetPointForNextLevel(sumPoint);

        return new MyPageLevelInfoResponse(memberLevel.getNumber(), sumPoint, pointForNextLevel);
    }

    private static MemberLevel getMemberLevelBySumPoint(final int sumPoint) {
        return MemberLevel.findByPoint(new Point(sumPoint))
                .orElseThrow(() -> {
                    final String errorMessage = String.format("포인트에 해당하는 레벨이 존재하지 않습니다. ( 포인트 : %d )", sumPoint);
                    return new ApiException(ExceptionTag.MEMBER_LEVEL, errorMessage);
                });
    }

    private List<FriendResponse> getFriends(final Long memberId, final LocalDate date) {
        final List<Long> friendIds = friendProvider.getByMemberId(memberId);

        return friendIds.stream()
                .map(friendId -> {
                    final String nickname = memberRepository.findMemberNicknameByMemberId(friendId);
                    final int sumPoint = memberPointRepository.findSumByMemberId(friendId);
                    final MemberLevel memberLevel = getMemberLevelBySumPoint(sumPoint);
                    final Long weeklyExpenditure = weeklyExpenditureProvider.getByMemberId(friendId, date);
                    return new FriendResponse(friendId, memberLevel.getNumber(), nickname, weeklyExpenditure);
                })
                .toList();
    }

    private List<MyPageExpenditureResponse> getExpenditure(final Long memberId) {
        final List<ExpenditureDto> expenditures = expenditureProvider.getByMemberIdPageable(memberId,
                                                                                            EXPENDITURE_PAGEABLE);

        return expenditures.stream()
                .map(MyPageExpenditureResponse::from)
                .toList();
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Id 에 해당하는 회원이 존재하지 않습니다. ( ID : %d )", memberId);
                    return new ApiException(ExceptionTag.MEMBER_FIND, errorMessage);
                });
    }
}
