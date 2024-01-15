package com.poolex.poolex.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poolex.poolex.goal.domain.Goal;
import com.poolex.poolex.goal.domain.GoalRepository;
import com.poolex.poolex.goal.domain.GoalType;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.time.LocalDate;
import java.util.Optional;
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
}
