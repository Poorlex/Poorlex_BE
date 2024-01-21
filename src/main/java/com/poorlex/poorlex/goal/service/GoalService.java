package com.poorlex.poorlex.goal.service;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.request.GoalModifyRequest;
import com.poorlex.poorlex.goal.service.dto.request.GoalUpdateRequest;
import com.poorlex.poorlex.goal.service.dto.request.MemberGoalRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalTypeResponse;
import com.poorlex.poorlex.member.domain.MemberRepository;
import java.time.LocalDate;
import java.util.Arrays;
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

    @Transactional
    public Long createGoal(final Long memberId, final GoalCreateRequest request) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("해당 Id의 멤버가 존재하지 않습니다.");
        }
        final Goal goal = goalRepository.save(mapToGoal(memberId, request));
        return goal.getId();
    }

    private Goal mapToGoal(final Long memberId, final GoalModifyRequest request) {
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

    public List<GoalTypeResponse> findAllGoalType() {
        return Arrays.stream(GoalType.values())
            .map(GoalTypeResponse::from)
            .toList();
    }

    @Transactional
    public void finishGoal(final Long memberId, final Long goalId) {
        final Goal goal = goalRepository.findById(goalId)
            .orElseThrow(IllegalArgumentException::new);
        validateGoalMemberId(memberId, goal);
        goal.finish();
    }

    private void validateGoalMemberId(final Long memberId, final Goal goal) {
        if (!goal.hasSameMemberId(memberId)) {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public void deleteGoal(final Long memberId, final Long goalId) {
        final Goal goal = goalRepository.findById(goalId)
            .orElseThrow(IllegalArgumentException::new);

        validateGoalMemberId(memberId, goal);
        goalRepository.delete(goal);
    }

    @Transactional
    public void updateGoal(final Long memberId, final Long goalId, final GoalUpdateRequest request) {
        final Goal goal = goalRepository.findById(goalId)
            .orElseThrow(IllegalArgumentException::new);

        validateGoalMemberId(memberId, goal);
        goal.pasteValueFieldsFrom(mapToGoal(null, request));
    }

    public List<GoalResponse> findMemberGoalWithStatus(final Long memberId,
                                                       final MemberGoalRequest request) {
        final GoalStatus goalStatus = GoalStatus.findByName(request.getStatus())
            .orElseThrow(() -> new IllegalArgumentException("해당 이름의 상태는 존재하지 않습니다."));
        if (goalStatus == GoalStatus.PROGRESS) {
            return findMemberProgressGoals(memberId, request.getDate());
        }
        return findMemberFinishedGoals(memberId);
    }

    public List<GoalResponse> findMemberProgressGoals(final Long memberId, final LocalDate date) {
        return goalRepository.findAllByMemberIdAndStatus(memberId, GoalStatus.PROGRESS)
            .stream()
            .map(goal -> GoalResponse.fromProgressGoal(goal, date))
            .toList();
    }

    public List<GoalResponse> findMemberFinishedGoals(final Long memberId) {
        return goalRepository.findAllByMemberIdAndStatus(memberId, GoalStatus.FINISH)
            .stream()
            .map(GoalResponse::fromFinishGoal)
            .toList();
    }
}
