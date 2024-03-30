package com.poorlex.poorlex.alarm.alarmallowance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlarmAllowance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Column(name = "expenditure_request")
    private boolean isAllowExpenditureRequestAlarm;

    @Column(name = "battle_status")
    private boolean isAllowBattleStatusAlarm;

    @Column(name = "battle_chat")
    private boolean isAllowBattleChatAlarm;

    @Column(name = "friend")
    private boolean isAllowFriendAlarm;

    @Column(name = "battle_invite")
    private boolean isAllowBattleInvitationAlarm;

    public AlarmAllowance(final Long id,
                          final Long memberId,
                          final boolean isAllowExpenditureRequestAlarm,
                          final boolean isAllowBattleStatusAlarm,
                          final boolean isAllowBattleChatAlarm,
                          final boolean isAllowFriendAlarm,
                          final boolean isAllowBattleInvitationAlarm) {
        this.id = id;
        this.memberId = memberId;
        this.isAllowExpenditureRequestAlarm = isAllowExpenditureRequestAlarm;
        this.isAllowBattleStatusAlarm = isAllowBattleStatusAlarm;
        this.isAllowBattleChatAlarm = isAllowBattleChatAlarm;
        this.isAllowFriendAlarm = isAllowFriendAlarm;
        this.isAllowBattleInvitationAlarm = isAllowBattleInvitationAlarm;
    }

    public static AlarmAllowance withoutId(final Long memberId,
                                           final boolean isAllowExpenditureRequestAlarm,
                                           final boolean isAllowBattleStatusAlarm,
                                           final boolean isAllowBattleChatAlarm,
                                           final boolean isAllowFriendAlarm,
                                           final boolean isAllowBattleInvitationAlarm) {
        return new AlarmAllowance(null,
                                  memberId,
                                  isAllowExpenditureRequestAlarm,
                                  isAllowBattleStatusAlarm,
                                  isAllowBattleChatAlarm,
                                  isAllowFriendAlarm,
                                  isAllowBattleInvitationAlarm);
    }

    public static AlarmAllowance withoutIdWithAllAllowed(final Long memberId) {
        return new AlarmAllowance(null, memberId, true, true, true, true, true);
    }

    public void update(final AlarmAllowanceType type, final boolean isAllow) {
        switch (type) {
            case EXPENDITURE_REQUEST -> setExpenditureRequestAlarmAllowance(isAllow);
            case BATTLE_STATUS -> setBattleStatusAlarmAllowance(isAllow);
            case BATTLE_CHAT -> setBattleChatAlarmAllowance(isAllow);
            case FRIEND -> setFriendAlarmAllowance(isAllow);
            case BATTLE_INVITE -> setBattleInvitationAlarmAllowance(isAllow);
        }
    }

    private void setExpenditureRequestAlarmAllowance(final boolean isAllowed) {
        this.isAllowExpenditureRequestAlarm = isAllowed;
    }

    private void setBattleStatusAlarmAllowance(final boolean isAllowed) {
        this.isAllowBattleStatusAlarm = isAllowed;
    }

    private void setBattleChatAlarmAllowance(final boolean isAllowed) {
        this.isAllowBattleChatAlarm = isAllowed;
    }

    private void setFriendAlarmAllowance(final boolean isAllowed) {
        this.isAllowFriendAlarm = isAllowed;
    }

    private void setBattleInvitationAlarmAllowance(final boolean isAllowed) {
        this.isAllowBattleInvitationAlarm = isAllowed;
    }
}
