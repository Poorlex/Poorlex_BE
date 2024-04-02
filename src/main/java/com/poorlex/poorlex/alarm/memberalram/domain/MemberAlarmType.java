package com.poorlex.poorlex.alarm.memberalram.domain;

import java.util.List;

public enum MemberAlarmType {
    FRIEND_INVITATION_NOT_ACCEPTED,
    FRIEND_INVITATION_ACCEPTED,
    FRIEND_INVITATION_DENIED,
    FRIEND_ACCEPTED,
    BATTLE_INVITATION_NOT_ACCEPTED,
    BATTLE_INVITATION_ACCEPTED,
    BATTLE_INVITATION_DENIED,
    BATTLE_INVITATION_ACCEPT,
    BATTLE_KICKED_OUT;
    public static final List<MemberAlarmType> FRIEND_ALARM_TYPES = List.of(
            FRIEND_INVITATION_NOT_ACCEPTED,
            FRIEND_INVITATION_ACCEPTED,
            FRIEND_INVITATION_DENIED,
            FRIEND_ACCEPTED
    );

    public static final List<MemberAlarmType> BATTLE_INVITE_ALARM_TYPES = List.of(
            BATTLE_INVITATION_NOT_ACCEPTED,
            BATTLE_INVITATION_ACCEPTED,
            BATTLE_INVITATION_DENIED,
            BATTLE_INVITATION_ACCEPT
    );

    public static boolean isFriendTypeAlarm(final MemberAlarmType memberAlarmType) {
        return FRIEND_ALARM_TYPES.contains(memberAlarmType);
    }

    public static boolean isBattleInvitedTypeAlarm(final MemberAlarmType memberAlarmType) {
        return BATTLE_INVITE_ALARM_TYPES.contains(memberAlarmType);
    }
}
