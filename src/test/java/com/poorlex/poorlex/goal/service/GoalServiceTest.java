package com.poorlex.poorlex.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.goal.domain.Goal;
import com.poorlex.poorlex.goal.domain.GoalAmount;
import com.poorlex.poorlex.goal.domain.GoalDuration;
import com.poorlex.poorlex.goal.domain.GoalDurationType;
import com.poorlex.poorlex.goal.domain.GoalName;
import com.poorlex.poorlex.goal.domain.GoalRepository;
import com.poorlex.poorlex.goal.domain.GoalStatus;
import com.poorlex.poorlex.goal.domain.GoalType;
import com.poorlex.poorlex.goal.service.dto.request.GoalCreateRequest;
import com.poorlex.poorlex.goal.service.dto.request.GoalUpdateRequest;
import com.poorlex.poorlex.goal.service.dto.request.MemberGoalRequest;
import com.poorlex.poorlex.goal.service.dto.response.GoalIdResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalResponse;
import com.poorlex.poorlex.goal.service.dto.response.GoalTypeResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.time.LocalDate;
import java.util.Arrays;
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
    void 목표를_수정한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());
        final GoalUpdateRequest request = new GoalUpdateRequest(
            GoalType.REST_AND_REFRESH.name(),
            "목표명",
            10000,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        );

        //when
        goalService.updateGoal(member.getId(), goal.getId(), request);

        //then
        final Goal updatedGoal = goalRepository.findById(goal.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertSoftly(
            softly -> {
                softly.assertThat(updatedGoal.getAmount()).isEqualTo(request.getAmount());
                softly.assertThat(updatedGoal.getType().name()).isEqualTo(request.getType());
                softly.assertThat(updatedGoal.getName()).isEqualTo(request.getName());
                softly.assertThat(updatedGoal.getStartDate()).isEqualTo(request.getStartDate());
                softly.assertThat(updatedGoal.getEndDate()).isEqualTo(request.getEndDate());
            }
        );
    }

    @Test
    void 목표를_삭제한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());

        //when
        goalService.deleteGoal(member.getId(), goal.getId());

        //then
        final Optional<Goal> findGoal = goalRepository.findById(goal.getId());
        assertThat(findGoal).isEmpty();
    }

    @Test
    void 목표를_완료한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final Goal goal = saveGoalWithMemberId(member.getId());

        //when
        goalService.finishGoal(member.getId(), goal.getId());

        //then
        final Goal updatedGoal = goalRepository.findById(goal.getId())
            .orElseThrow(IllegalArgumentException::new);
        assertThat(updatedGoal.getStatus()).isEqualTo(GoalStatus.FINISH);
    }

    @Test
    void 목표타입_목록을_조회한다() {
        //given
        //when
        final List<GoalTypeResponse> responses = goalService.findAllGoalType();

        //then
        final List<GoalTypeResponse> expected = Arrays.stream(GoalType.values())
            .map(goalType -> new GoalTypeResponse(goalType.getName(), goalType.getRecommendNames()))
            .toList();
        assertThat(responses).usingRecursiveComparison().isEqualTo(expected);
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
    void 회원이_진행중인_목표들을_조회한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final LocalDate requestDate = LocalDate.now();
        final Goal goal1 = saveGoalWithMemberId(member.getId(), requestDate, requestDate.plusDays(100));
        final Goal goal2 = saveGoalWithMemberId(member.getId(), requestDate, requestDate.plusYears(2));
        final MemberGoalRequest request = new MemberGoalRequest("PROGRESS", requestDate);

        //when
        final List<GoalResponse> progressGoals = goalService.findMemberGoalWithStatus(member.getId(), request);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(progressGoals).hasSize(2);

                final GoalResponse firstGoalResponse = progressGoals.get(0);
                softly.assertThat(firstGoalResponse.getName()).isEqualTo(goal1.getName());
                softly.assertThat(firstGoalResponse.getAmount()).isEqualTo(goal1.getAmount());
                softly.assertThat(firstGoalResponse.getDurationType()).isEqualTo(GoalDurationType.SHORT.name());
                softly.assertThat(firstGoalResponse.getDayLeft()).isEqualTo(100);
                softly.assertThat(firstGoalResponse.getMonthLeft()).isEqualTo(goal1.getMonthLeft(requestDate));

                final GoalResponse secondGoalResponse = progressGoals.get(1);
                softly.assertThat(secondGoalResponse.getName()).isEqualTo(goal2.getName());
                softly.assertThat(secondGoalResponse.getAmount()).isEqualTo(goal2.getAmount());
                softly.assertThat(secondGoalResponse.getDurationType()).isEqualTo(GoalDurationType.MIDDLE.name());
                softly.assertThat(secondGoalResponse.getDayLeft()).isEqualTo(goal2.getDayLeft(requestDate));
                softly.assertThat(secondGoalResponse.getMonthLeft()).isEqualTo(goal2.getMonthLeft(requestDate));
            }
        );
    }

    @Test
    void 회원의_완료된_목표들을_조회한다() {
        //given
        final Member member = memberRepository.save(Member.withoutId("oauthId", new MemberNickname("nickname")));
        final LocalDate requestDate = LocalDate.now();
        final Goal goal1 = saveGoalWithMemberId(member.getId(), requestDate, requestDate.plusDays(100));
        final Goal goal2 = saveGoalWithMemberId(member.getId(), requestDate, requestDate.plusYears(2));
        goal1.finish();
        final MemberGoalRequest request = new MemberGoalRequest("FINISH", requestDate);

        //when
        final List<GoalResponse> progressGoals = goalService.findMemberGoalWithStatus(member.getId(), request);

        //then
        assertSoftly(
            softly -> {
                softly.assertThat(progressGoals).hasSize(1);

                final GoalResponse firstGoalResponse = progressGoals.get(0);
                softly.assertThat(firstGoalResponse.getName()).isEqualTo(goal1.getName());
                softly.assertThat(firstGoalResponse.getAmount()).isEqualTo(goal1.getAmount());
                softly.assertThat(firstGoalResponse.getDurationType()).isEqualTo(GoalDurationType.SHORT.name());
                softly.assertThat(firstGoalResponse.getDayLeft()).isEqualTo(0);
                softly.assertThat(firstGoalResponse.getMonthLeft()).isEqualTo(0);
                softly.assertThat(firstGoalResponse.getStartDate()).isEqualTo(goal1.getStartDate());
                softly.assertThat(firstGoalResponse.getEndDate()).isEqualTo(goal1.getEndDate());
            }
        );
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

    private Goal saveGoalWithMemberId(final Long memberId, final LocalDate start, final LocalDate end) {
        final GoalName goalName = new GoalName("목표명");
        final GoalType goalType = GoalType.REST_AND_REFRESH;
        final GoalDuration goalDuration = new GoalDuration(start, end);
        final GoalAmount goalAmount = new GoalAmount(10000);
        final Goal goal = Goal.withoutId(memberId, goalType, goalName, goalAmount, goalDuration, GoalStatus.PROGRESS);

        return goalRepository.save(goal);
    }
}
