package com.poorlex.poorlex.chat.service.event;

import com.poorlex.poorlex.chat.domain.MessageType;

public record ChattingSentEvent(Long battleId, Long memberId, String content, MessageType type) {
}
