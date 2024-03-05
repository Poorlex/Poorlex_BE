package com.poorlex.refactoring.user.member.service;

import com.poorlex.refactoring.user.member.domain.Member;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import com.poorlex.refactoring.user.member.service.dto.FriendMemberIdDto;
import com.poorlex.refactoring.user.member.service.dto.response.BattleSuccessCountResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageExpenditureResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageExpendituresResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageFriendResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageFriendsResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageLevelInfoResponse;
import com.poorlex.refactoring.user.member.service.dto.response.MyPageResponse;
import com.poorlex.refactoring.user.member.service.provider.ExpenditureProvider;
import com.poorlex.refactoring.user.member.service.provider.FriendCountProvider;
import com.poorlex.refactoring.user.member.service.provider.FriendIdProvider;
import com.poorlex.refactoring.user.member.service.provider.LevelProvider;
import com.poorlex.refactoring.user.member.service.provider.MemberBattleSuccessCountProvider;
import com.poorlex.refactoring.user.member.service.provider.MemberLevelInfoProvider;
import com.poorlex.refactoring.user.member.service.provider.WeeklyExpenditureProvider;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private static final int MY_PAGE_EXPOSE_FRIEND_COUNT = 4;
    private static final int MY_PAGE_EXPOSE_EXPENDITURE_COUNT = 4;

    private final MemberRepository memberRepository;
    private final FriendIdProvider friendIdProvider;
    private final FriendCountProvider friendCountProvider;
    private final LevelProvider levelProvider;
    private final ExpenditureProvider expenditureProvider;
    private final WeeklyExpenditureProvider weeklyExpenditureProvider;
    private final MemberLevelInfoProvider memberLevelInfoProvider;
    private final MemberBattleSuccessCountProvider memberBattleSuccessCountProvider;

    public MyPageResponse getMyPageInfo(final Long memberId) {
        return getMyPageInfo(memberId, LocalDate.now());
    }

    public MyPageResponse getMyPageInfo(final Long memberId, final LocalDate date) {
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(IllegalArgumentException::new);
        final String nickname = member.getNickname();
        final String description = member.getDescription().orElse(null);
        final MyPageLevelInfoResponse levelInfo = getLevelInfo(memberId);
        final BattleSuccessCountResponse battleSuccessCounts = getBattleDifficultySuccessCount(memberId);
        final MyPageFriendsResponse friends = getFriends(memberId, date);
        final MyPageExpendituresResponse expenditures = getExpenditures(memberId);

        return new MyPageResponse(nickname, description, levelInfo, battleSuccessCounts, friends, expenditures);
    }

    private MyPageLevelInfoResponse getLevelInfo(final Long memberId) {
        return MyPageLevelInfoResponse.from(memberLevelInfoProvider.byMemberId(memberId));
    }

    private BattleSuccessCountResponse getBattleDifficultySuccessCount(final Long memberId) {
        return BattleSuccessCountResponse.from(memberBattleSuccessCountProvider.byMemberId(memberId));
    }

    private MyPageExpendituresResponse getExpenditures(final Long memberId) {
        final int expenditureCount = expenditureProvider.countByMemberId(memberId);
        final List<MyPageExpenditureResponse> expenditureResponses =
            expenditureProvider.byMemberIdLimit(memberId, MY_PAGE_EXPOSE_EXPENDITURE_COUNT).stream()
                .map(MyPageExpenditureResponse::from)
                .toList();

        return new MyPageExpendituresResponse(expenditureCount, expenditureResponses);
    }

    private MyPageFriendsResponse getFriends(final Long memberId, final LocalDate date) {
        final int totalFriendCount = friendCountProvider.byMemberId(memberId);
        final List<FriendMemberIdDto> friendMemberIdDtos =
            friendIdProvider.byMemberIdLimit(memberId, MY_PAGE_EXPOSE_FRIEND_COUNT);
        final List<MyPageFriendResponse> friendResponses = friendMemberIdDtos.stream()
            .map(friendMemberIdDto -> getFriendResponse(friendMemberIdDto.getMemberId(), date))
            .toList();

        return new MyPageFriendsResponse(totalFriendCount, friendResponses);
    }

    private MyPageFriendResponse getFriendResponse(final Long friendMemberId, final LocalDate date) {
        final String nickname = memberRepository.findMemberNicknameByMemberId(friendMemberId);
        final int level = levelProvider.byMemberId(friendMemberId);
        final Long weeklyExpenditure = weeklyExpenditureProvider.byMemberIdContains(friendMemberId, date);

        return new MyPageFriendResponse(friendMemberId, level, nickname, weeklyExpenditure);
    }
}
