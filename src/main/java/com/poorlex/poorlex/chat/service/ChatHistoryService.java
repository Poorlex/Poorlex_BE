package com.poorlex.poorlex.chat.service;

import com.poorlex.poorlex.chat.domain.Chatting;
import com.poorlex.poorlex.chat.domain.ChattingQueryRepository;
import com.poorlex.poorlex.chat.domain.ChattingRepository;
import com.poorlex.poorlex.chat.service.dto.response.ChatHistoryResponse;
import com.poorlex.poorlex.chat.service.event.ChattingSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatHistoryService {

    private final ChattingQueryRepository chattingQueryRepository;
    private final ChattingRepository chattingRepository;

    public List<ChatHistoryResponse> loadChatHistory(Long battleId, Pageable pageable) {
        return chattingQueryRepository.findByBattleId(battleId, pageable);
    }

    @TransactionalEventListener(value = ChattingSentEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handleMessageSent(ChattingSentEvent event) {
        Chatting chatting = Chatting.withoutId(event.battleId(), event.memberId(), event.type(), event.content());
        chattingRepository.save(chatting);
    }
}
