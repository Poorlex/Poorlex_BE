package com.poorlex.poorlex.voting.votingpaper.controller;

import com.poorlex.poorlex.security.service.MemberInfo;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import com.poorlex.poorlex.voting.votingpaper.service.dto.request.VotingPaperCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class VotingPaperController {

    private final VotingPaperService votingPaperService;

    @PostMapping("/battles/{battleId}/votes/{voteId}/vote")
    public ResponseEntity<Void> createVotingPaper(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                  @PathVariable(name = "voteId") final Long voteId,
                                                  @RequestBody final VotingPaperCreateRequest request) {
        votingPaperService.createVotingPaper(memberInfo.getId(), voteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
