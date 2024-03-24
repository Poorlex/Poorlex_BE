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

    //AWS 관련
    AWS_S3("S3 관련 에러");

    private final String tag;

    ExceptionTag(final String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return tag;
    }
}
