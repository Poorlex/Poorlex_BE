package com.poorlex.poorlex.chat.service.dto.response;

import com.poorlex.poorlex.chat.domain.MessageType;

public record ChatHistoryResponse(Long memberId, String content, MessageType type) {
}
