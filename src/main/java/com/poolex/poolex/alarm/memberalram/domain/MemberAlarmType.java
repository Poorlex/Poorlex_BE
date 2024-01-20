package com.poolex.poolex.alarm.memberalram.domain;

import java.util.List;

public enum MemberAlarmType {
    FRIEND_INVITATION,
    FRIEND_ACCEPTED,
    FRIEND_DENIED,
    BATTLE_INVITATION,
    BATTLE_INVITATION_ACCEPT,
    BATTLE_KICKED_OUT;
    private static final List<MemberAlarmType> FRIEND_ALARM_TYPES = List.of(
        FRIEND_INVITATION,
        FRIEND_ACCEPTED,
        FRIEND_DENIED
    );

    private static final List<MemberAlarmType> BATTLE_INVITE_ALARM_TYPES = List.of(
        BATTLE_INVITATION,
        BATTLE_INVITATION_ACCEPT
    );

    public static boolean isFriendTypeAlarm(final MemberAlarmType memberAlarmType) {
        return FRIEND_ALARM_TYPES.contains(memberAlarmType);
    }

    public static boolean isBattleInvitedTypeAlarm(final MemberAlarmType memberAlarmType) {
        return BATTLE_INVITE_ALARM_TYPES.contains(memberAlarmType);
    }
}
