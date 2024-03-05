package com.poorlex.refactoring.battle.history.service.validation;

public class BattleHistoryException extends RuntimeException {

    public BattleHistoryException() {
    }

    public static class BattleNotExistException extends BattleHistoryException {

        public BattleNotExistException() {
        }
    }
}
