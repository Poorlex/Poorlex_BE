package com.poorlex.refactoring.expenditure.service.validate;

public class ExpenditureException extends RuntimeException {

    public ExpenditureException(final String message) {
        super(message);
    }

    public static class ExpenditureImageNotExistException extends ExpenditureException {

        public ExpenditureImageNotExistException() {
            super("지출 이미지가 존재하지 않습니다.");
        }

        public ExpenditureImageNotExistException(final String message) {
            super(message);
        }
    }

    public static class NoGrantForUpdateExpenditureException extends ExpenditureException {

        public NoGrantForUpdateExpenditureException() {
            super("지출 수정은 당사자만 가능합니다.");
        }

        public NoGrantForUpdateExpenditureException(final String message) {
            super(message);
        }
    }
}
