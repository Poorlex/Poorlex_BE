package com.poorlex.refactoring.battle.battle.domain;

public class BattleCreateException extends RuntimeException {

    public BattleCreateException(final String message) {
        super(message);
    }

    public static class BattleNameException extends BattleCreateException {

        public BattleNameException(final String message) {
            super(message);
        }
    }

    public static class BattleIntroductionException extends BattleCreateException {

        public BattleIntroductionException(final String message) {
            super(message);
        }
    }

    public static class BattleImageException extends BattleCreateException {

        public BattleImageException(final String message) {
            super(message);
        }
    }

    public static class BattleBudgetException extends BattleCreateException {

        public BattleBudgetException(final String message) {
            super(message);
        }
    }

    public static class BattleDurationException extends BattleCreateException {

        public BattleDurationException(final String message) {
            super(message);
        }
    }
}
