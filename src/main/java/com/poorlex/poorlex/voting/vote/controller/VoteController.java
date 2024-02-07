package com.poorlex.poorlex.voting.vote.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/battles/{battleId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> createVote(@MemberOnly final MemberInfo memberInfo,
                                           @PathVariable(name = "battleId") final Long battleId,
                                           @RequestBody final VoteCreateRequest voteCreateRequest) {
        voteService.createVote(memberInfo.getMemberId(), battleId, voteCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
