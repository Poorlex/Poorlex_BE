package com.poorlex.poorlex.auth.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleLoginResponse(@JsonProperty("id_token") String idToken) {
}
