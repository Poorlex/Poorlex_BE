package com.poorlex.poorlex.voting.vote.service.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VoteCreateRequest {

    private final int amount;
    private final LocalDateTime start;
    private final int duration;
    private final String name;
}
