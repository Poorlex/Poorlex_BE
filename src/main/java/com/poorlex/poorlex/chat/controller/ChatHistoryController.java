package com.poorlex.poorlex.chat.controller;

import com.poorlex.poorlex.chat.service.ChatHistoryService;
import com.poorlex.poorlex.chat.service.dto.response.ChatHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping("/battles/{battleId}")
    public ResponseEntity<List<ChatHistoryResponse>> loadChatHistory(@PathVariable Long battleId, @ParameterObject Pageable pageable) {
        List<ChatHistoryResponse> response = chatHistoryService.loadChatHistory(battleId, pageable);
        return ResponseEntity.ok(response);
    }
}
