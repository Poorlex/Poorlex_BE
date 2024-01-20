package com.poorlex.poorlex.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GoalServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private GoalRepository goalRepository;
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        this.goalService = new GoalService(goalRepository, memberRepository);
    }

    @Test
    void 목표를_생성한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final GoalCreateRequest request = new GoalCreateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        final Long createdGoalId = goalService.createGoal(member.getId(), request);

        //then
        final Optional<Goal> findGoal = goalRepository.findById(createdGoalId);
        assertThat(findGoal).isPresent();
    }

    @Test
    void 목표를_생성할_때_멤버ID에_해당하는_멤버가_없으면_예외를_던진다() {
        //given
        final long notExistMemberId = 1L;
        final GoalCreateRequest request = new GoalCreateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        //then
        assertThatThrownBy(() -> goalService.createGoal(notExistMemberId, request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 회원이_등록한_목표들의_Id를_조회한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal1 = saveGoalWithMemberId(member.getId());
        final Goal goal2 = saveGoalWithMemberId(member.getId());
        final Goal goal3 = saveGoalWithMemberId(member.getId());

        //when
        final List<GoalIdResponse> memberGoalIds = goalService.findMemberGoalIds(member.getId());

        //then
        final List<GoalIdResponse> goalIds = Stream.of(goal1, goal2, goal3)
            .map(goal -> new GoalIdResponse(goal.getId()))
            .toList();

        assertThat(memberGoalIds).usingRecursiveComparison().isEqualTo(goalIds);
    }

    private Goal saveGoalWithMemberId(final Long memberId) {
        final GoalName goalName = new GoalName("목표명");
        final GoalType goalType = GoalType.REST_AND_REFRESH;
        final GoalDuration goalDuration = new GoalDuration(LocalDate.now().minusDays(1), LocalDate.now());
        final GoalAmount goalAmount = new GoalAmount(10000);
        final Goal goal = Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);

        return goalRepository.save(goal);
    }
}
