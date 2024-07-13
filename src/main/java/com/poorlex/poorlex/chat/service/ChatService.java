package com.poorlex.poorlex.chat.service;

import com.poorlex.poorlex.battle.participation.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.battle.participation.service.event.BattleParticipantWithdrawEvent;
import com.poorlex.poorlex.chat.service.dto.request.BattleRoomMessage;
import com.poorlex.poorlex.chat.domain.MessageType;
import com.poorlex.poorlex.chat.service.event.ChattingSentEvent;
import com.poorlex.poorlex.user.member.service.MemberQueryService;
import com.poorlex.poorlex.user.member.service.dto.response.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ApplicationEventPublisher publisher;
    private final MemberQueryService memberQueryService;

    @Transactional
    public void sendMessage(BattleRoomMessage battleRoomMessage, Long battleId, Long memberId) {
        simpMessageSendingOperations.convertAndSend("/topic/battle/" + battleId, battleRoomMessage);
        publisher.publishEvent(new ChattingSentEvent(battleId, memberId, battleRoomMessage.content(), MessageType.CHAT));
    }

    @TransactionalEventListener(value = BattleParticipantAddedEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void sendJoiningMessage(BattleParticipantAddedEvent event) {
        MemberProfileResponse memberProfile = memberQueryService.getMemberProfile(event.getMemberId());
        BattleRoomMessage payload = new BattleRoomMessage(memberProfile.nickname(), null, MessageType.JOIN);

        simpMessageSendingOperations.convertAndSend("/topic/battle/" + event.getBattleId(), payload);
        publisher.publishEvent(new ChattingSentEvent(event.getBattleId(), event.getMemberId(), null, MessageType.JOIN));
    }

    @TransactionalEventListener(value = BattleParticipantWithdrawEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void sendLeavingMessage(BattleParticipantWithdrawEvent event) {
        MemberProfileResponse memberProfile = memberQueryService.getMemberProfile(event.memberId());
        BattleRoomMessage payload = new BattleRoomMessage(memberProfile.nickname(), null, MessageType.LEAVE);

        simpMessageSendingOperations.convertAndSend("/topic/battle/" + event.battleId(), payload);
        publisher.publishEvent(new ChattingSentEvent(event.battleId(), event.memberId(), null, MessageType.LEAVE));
    }
}
