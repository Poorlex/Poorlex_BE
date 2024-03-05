package com.poorlex.refactoring.user.member.service.validation.exception;

public class MemberException extends RuntimeException {

    public MemberException(final String message) {
        super(message);
    }

    public static class NotUpdatableNicknameException extends MemberException {

        public NotUpdatableNicknameException() {
            super("변경가능한 닉네임이 아닙니다.");
        }

        public NotUpdatableNicknameException(final String message) {
            super(message);
        }
    }

    public static class NotUpdatableDescriptionException extends MemberException {

        public NotUpdatableDescriptionException() {
            super("변경가능한 소개가 아닙니다.");
        }

        public NotUpdatableDescriptionException(final String message) {
            super(message);
        }
    }
}
