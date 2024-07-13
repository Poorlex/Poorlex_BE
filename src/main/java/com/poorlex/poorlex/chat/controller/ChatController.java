package com.poorlex.poorlex.chat.controller;

import com.poorlex.poorlex.chat.service.ChatService;
import com.poorlex.poorlex.chat.service.dto.request.BattleRoomMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/battle/{battleId}")
    public BattleRoomMessage sendMessage(@Payload BattleRoomMessage battleRoomMessage,
                                         SimpMessageHeaderAccessor accessor,
                                         @DestinationVariable("battleId") Long battleId) {
        chatService.sendMessage(battleRoomMessage, battleId, Long.parseLong(accessor.getUser().getName()));
        return battleRoomMessage;
    }

    @SubscribeMapping("/battle/{battleId}")
    public void joinBattle(
                                        @DestinationVariable Long battleId,
                                        SimpMessageHeaderAccessor accessor
                                        ) {
        log.info("{} joined battle {}", accessor.getUser().getName(), battleId);
    }
}
