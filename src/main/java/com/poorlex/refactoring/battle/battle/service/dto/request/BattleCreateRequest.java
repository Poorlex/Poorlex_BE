package com.poorlex.refactoring.battle.battle.service.dto.request;

public record BattleCreateRequest(String name,
                                  String introduction,
                                  String imageUrl,
                                  Long budget,
                                  int maxParticipantSize) {

}
