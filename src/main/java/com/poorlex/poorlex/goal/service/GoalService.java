package com.poorlex.poorlex.goal.service;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.member.domain.MemberRepository;
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
