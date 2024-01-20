package com.poorlex.poorlex.alarm.memberalram.service;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.alarm.memberalram.service.dto.response.MemberAlarmResponse;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAlarmResponseConverter {

    private final MemberRepository memberRepository;
    private final BattleRepository battleRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    public List<MemberAlarmResponse> convert(final List<MemberAlarm> memberAlarms, final LocalDateTime dateTime) {
        return memberAlarms.stream()
            .map(memberAlarm -> convert(memberAlarm, dateTime))
            .toList();
    }

    public MemberAlarmResponse convert(final MemberAlarm memberAlarm, final LocalDateTime dateTime) {
        if (MemberAlarmType.isFriendTypeAlarm(memberAlarm.getType())) {
            return generateFriendTypeAlarmResponse(memberAlarm, dateTime);
        }

        if (MemberAlarmType.isBattleInvitedTypeAlarm(memberAlarm.getType())) {
            return generateBattleInvitationTypeAlarmResponse(memberAlarm, dateTime);
        }

        return generateBattleTypeAlarmResponse(memberAlarm, dateTime);
    }

    private MemberAlarmResponse generateFriendTypeAlarmResponse(final MemberAlarm memberAlarm,
                                                                final LocalDateTime dateTime) {
        return MemberAlarmResponse.from(memberAlarm, getFriendNickname(memberAlarm), null, dateTime);
    }

    private MemberAlarmResponse generateBattleInvitationTypeAlarmResponse(final MemberAlarm memberAlarm,
                                                                          final LocalDateTime dateTime) {
        final BattleParticipant battleParticipant = battleParticipantRepository.findById(memberAlarm.getTargetId())
            .orElseThrow(IllegalArgumentException::new);
        final String friendNickname = getFriendNickname(battleParticipant);
        final String battleName = getBattleName(battleParticipant);

        return MemberAlarmResponse.from(memberAlarm, friendNickname, battleName, dateTime);
    }

    private MemberAlarmResponse generateBattleTypeAlarmResponse(final MemberAlarm memberAlarm,
                                                                final LocalDateTime dateTime) {
        return MemberAlarmResponse.from(memberAlarm, null, getBattleName(memberAlarm), dateTime);
    }

    private String getFriendNickname(final MemberAlarm memberAlarm) {
        return memberRepository.findMemberNicknameByMemberId(memberAlarm.getTargetId());
    }

    private String getFriendNickname(final BattleParticipant battleParticipant) {
        return memberRepository.findMemberNicknameByMemberId(battleParticipant.getMemberId());
    }

    private String getBattleName(final MemberAlarm memberAlarm) {
        return battleRepository.findBattleNameById(memberAlarm.getTargetId());
    }

    private String getBattleName(final BattleParticipant battleParticipant) {
        return battleRepository.findBattleNameById(battleParticipant.getBattleId());
    }
}
