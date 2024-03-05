package com.poorlex.refactoring.battle.invitation.service.validate;

public class BattleInviteException extends RuntimeException {

    public static class InviteOnlyByFriendException extends BattleInviteException {

        public InviteOnlyByFriendException() {
            super();
        }
    }

    public static class AlreadyParticipatingBattleException extends BattleInviteException {

        public AlreadyParticipatingBattleException() {
            super();
        }
    }

    public static class NotParticipatingBattleException extends BattleInviteException {

        public NotParticipatingBattleException() {
            super();
        }
    }
}
