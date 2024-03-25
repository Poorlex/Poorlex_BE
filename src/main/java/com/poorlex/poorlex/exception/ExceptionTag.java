package com.poorlex.poorlex.exception;

public enum ExceptionTag {
    //회원 관련
    MEMBER_FIND("회원 조회 에러"),
    MEMBER_NICKNAME("회원 닉네임 에러"),
    MEMBER_INTRODUCTION("회원 소개 에러"),
    MEMBER_POINT("회원 포인트 에러"),
    MEMBER_LEVEL("회원 레벨 에러"),

    //지출 관련
    EXPENDITURE_AMOUNT("지출 가격 에러"),
    EXPENDITURE_DESCRIPTION("지출 설명 에러"),
    EXPENDITURE_IMAGE("지출 이미지 에러"),
    EXPENDITURE_UPDATE("지출 수정 에러"),
    EXPENDITURE_FIND("지출 조회 에러"),

    //배틀 관련
    BATTLE_FIND("배틀 조회 에러"),
    BATTLE_BUDGET("배틀 예산 에러"),
    BATTLE_PROGRESS("배틀 진행 에러"),
    BATTLE_PARTICIPANT_SIZE("배틀 참가 인원 에러"),
    BATTLE_INTRODUCTION("배틀 소개 에러"),
    BATTLE_IMAGE("배틀 이미지 에러"),
    BATTLE_STATUS("배틀 상태 에러"),
    BATTLE_DIFFICULTY("배틀 난이도 에러"),
    BATTLE_NAME("배틀 이름 에러"),
    BATTLE_TYPE("배틀 타입 에러"),

    // 배틀 참가 관련
    BATTLE_WITHDRAW("배틀 탈퇴 에러"),
    BATTLE_PARTICIPATE("배틀 참가 에러"),
    BATTLE_PARTICIPANT_FIND("배틀 참가자 조회 에러"),

    //AWS 관련
    AWS_S3("S3 관련 에러"),
    WEEKLY_BUDGET_AMOUNT("주간 예산 금액 에러"),
    WEEKLY_BUDGET_DURATION("주간 예산 기간 에러");

    private final String tag;

    ExceptionTag(final String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return tag;
    }
}
