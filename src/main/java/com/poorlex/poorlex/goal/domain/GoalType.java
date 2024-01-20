package com.poorlex.poorlex.goal.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum GoalType {
    STABLE_FUTURE("안정된 미래", List.of(
        new GoalName("목돈 마련"),
        new GoalName("대출 상환"),
        new GoalName("내 집 마련"),
        new GoalName("비상금 모으기"),
        new GoalName("1억 모으기"),
        new GoalName("결혼 자금"),
        new GoalName("노후 준비")
    )),
    WEALTH_AND_HONOR("부귀 영화", List.of(
        new GoalName("목돈 마련"),
        new GoalName("대출 상환"),
        new GoalName("내 집 마련"),
        new GoalName("비상금 모으기"),
        new GoalName("1억 모으기"),
        new GoalName("결혼 자금"),
        new GoalName("노후 준비")
    )),
    STRESS_RESOLVE("스트레스 해소", List.of(
        new GoalName("근사한 식사"),
        new GoalName("나에게 어울리는 옷 장만"),
        new GoalName("고생한 나를 위한 용돈"),
        new GoalName("취미 생활비")
    )),
    SUCCESSFUL_AT_WORK("일적인 성공", List.of(
        new GoalName("제 2외국어 배우기"),
        new GoalName("스터디 클래스"),
        new GoalName("자격증 취득하기")
    )),
    REST_AND_REFRESH("휴식과 리프레쉬", List.of(
        new GoalName("여행 자금 모으기"),
        new GoalName("휴가비"),
        new GoalName("고생한 나를 위한 용돈"),
        new GoalName("퇴사 비상자금")
    ));
    private final String name;
    private final List<GoalName> recommendNames;

    GoalType(final String name, final List<GoalName> recommendNames) {
        this.name = name;
        this.recommendNames = recommendNames;
    }

    public List<String> getRecommendNames() {
        return recommendNames.stream()
            .map(GoalName::getValue)
            .toList();
    }

    public String getName() {
        return name;
    }

    public static Optional<GoalType> findByName(final String name) {
        return Arrays.stream(values())
            .filter(type -> type.name().equals(name) || type.getName().equals(name))
            .findFirst();
    }
}
