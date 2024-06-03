package com.poorlex.poorlex.user.member.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NicknameGenerateResponse(@JsonProperty("data") String nickname) {
}
