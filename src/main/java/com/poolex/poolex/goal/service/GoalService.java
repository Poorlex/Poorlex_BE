package com.poolex.poolex.goal.service;

import com.poolex.poolex.goal.domain.Goal;
import com.poolex.poolex.goal.domain.GoalAmount;
import com.poolex.poolex.goal.domain.GoalDuration;
import com.poolex.poolex.goal.domain.GoalName;
import com.poolex.poolex.goal.domain.GoalRepository;
import com.poolex.poolex.goal.domain.GoalStatus;
import com.poolex.poolex.goal.domain.GoalType;
import com.poolex.poolex.goal.service.dto.request.GoalCreateRequest;
import com.poolex.poolex.goal.service.dto.response.GoalIdResponse;
import com.poolex.poolex.member.domain.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final MemberRepository memberRepository;

    public Long createGoal(final Long memberId, final GoalCreateRequest request) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("해당 Id의 멤버가 존재하지 않습니다.");
        }
        final Goal goal = goalRepository.save(mapToGoal(memberId, request));
        return goal.getId();
    }

    private Goal mapToGoal(final Long memberId, final GoalCreateRequest request) {
        final GoalName goalName = new GoalName(request.getName());
        final GoalType goalType = GoalType.findByName(request.getType())
            .orElseThrow(IllegalArgumentException::new);
        final GoalDuration goalDuration = new GoalDuration(request.getStartDate(), request.getEndDate());
        final GoalAmount goalAmount = new GoalAmount(request.getAmount());

        return Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);
    }

    public List<GoalIdResponse> findMemberGoalIds(final Long memberId) {
        final List<Long> memberGoalIds = goalRepository.findIdsByMemberId(memberId);
        return memberGoalIds.stream()
            .map(GoalIdResponse::new)
            .toList();
    }
}
