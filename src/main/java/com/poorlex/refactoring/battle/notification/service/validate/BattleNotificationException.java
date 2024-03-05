package com.poorlex.refactoring.battle.notification.service.validate;

public class BattleNotificationException extends RuntimeException {

    public BattleNotificationException(final String message) {
        super(message);
    }

    public static class NotManagerException extends BattleNotificationException {

        public NotManagerException() {
            super("매니저 권한이 필요합니다.");
        }

        public NotManagerException(final String message) {
            super(message);
        }
    }
}
