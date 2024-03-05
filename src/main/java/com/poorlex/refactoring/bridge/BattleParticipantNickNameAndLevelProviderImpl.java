package com.poorlex.refactoring.bridge;

import com.poorlex.refactoring.battle.battle.service.dto.BattleParticipantNicknameAndLevelDto;
import com.poorlex.refactoring.battle.battle.service.provider.BattleParticipantNickNameAndLevelProvider;
import com.poorlex.refactoring.user.member.domain.Member;
import com.poorlex.refactoring.user.member.domain.MemberRepository;
import com.poorlex.refactoring.user.member.service.provider.MemberLevelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BattleParticipantNickNameAndLevelProviderImpl implements BattleParticipantNickNameAndLevelProvider {

    private final MemberRepository memberRepository;
    private final MemberLevelProvider memberLevelProvider;

    @Override
    public BattleParticipantNicknameAndLevelDto byMemberId(
        final Long participantMemberId) throws IllegalArgumentException {
        final Member member = memberRepository.findById(participantMemberId)
            .orElseThrow(IllegalArgumentException::new);
        final int level = memberLevelProvider.byMemberId(participantMemberId);

        return new BattleParticipantNicknameAndLevelDto(member.getNickname(), level);
    }
}
