package com.poorlex.refactoring.battle.battle.service.validation;

class BattleException extends RuntimeException {

    protected BattleException(final String message) {
        super(message);
    }

    static class BattleNotExistException extends BattleException {

        BattleNotExistException(final String message) {
            super(message);
        }
    }

    static class MaxBattleSizeException extends RuntimeException {

        MaxBattleSizeException(final String message) {
            super(message);
        }
    }

}
