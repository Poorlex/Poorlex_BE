package com.poorlex.poorlex.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final Map<String, String> simpSessionIdToBattleId = new HashMap<>();

    // TODO: Subscribe, Unsubscribe 이벤트 리스너로 온라인 유저 구현
//    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        simpSessionIdToBattleId.remove(event.getSessionId());
    }

//    @EventListener
    public void handleConnectedEvent(SessionConnectedEvent sessionConnectedEvent) {
    }

//    @EventListener
//    @SendToUser
    public void handleSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        String simpSessionId =
                (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpSessionId");
        String topic = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpDestination");

        simpSessionIdToBattleId.put(simpSessionId, topic);
    }

//    @EventListener
//    @SendToUser
    public void handleUnSubscribeEvent(SessionUnsubscribeEvent unsubscribeEvent) {
        String simpSessionId = (String) unsubscribeEvent.getMessage().getHeaders().get("simpSessionId");
        simpSessionIdToBattleId.get(simpSessionId);
    }
}
