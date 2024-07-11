package com.poorlex.poorlex.config.websocket;

import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.security.filter.WebSocketTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketTokenFilter webSocketTokenFilter;
    private final BattleParticipantRepository battleParticipantRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenFilter);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public Map<Long, Set<Long>> battleParticipants() {
        List<BattleParticipant> all = battleParticipantRepository.findAll();
        Map<Long, Set<Long>> battleParticipants = new HashMap<>();
        for (BattleParticipant battleParticipant : all) {
            battleParticipants.putIfAbsent(battleParticipant.getBattleId(), new HashSet<>());
            battleParticipants.get(battleParticipant.getBattleId()).add(battleParticipant.getMemberId());
        }
        return battleParticipants;
    }
}
