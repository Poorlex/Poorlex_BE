package com.poorlex.poorlex.chat.service.dto.request;


import com.poorlex.poorlex.chat.domain.MessageType;

public record BattleRoomMessage(String sender, String content, MessageType type) {
}
