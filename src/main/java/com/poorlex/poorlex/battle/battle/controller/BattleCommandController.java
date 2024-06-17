package com.poorlex.poorlex.battle.battle.controller;

import com.poorlex.poorlex.battle.battle.service.BattleService;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleCreateRequest;
import java.net.URI;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/battles")
@RequiredArgsConstructor
public class BattleCommandController implements BattleCommandControllerSwaggerInterface {

    private final BattleService battleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createBattle(@AuthenticationPrincipal final MemberInfo memberInfo,
                                             @RequestPart("image") final MultipartFile image,
                                             @RequestParam final String name,
                                             @RequestParam final String introduction,
                                             @RequestParam final int budget,
                                             @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                                                                    introduction,
                                                                    budget,
                                                                    maxParticipantSize);
        final Long createdBattleId = battleService.create(memberInfo.getId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }

    @PostMapping(path = "/progressing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createProgressingBattle(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                        @RequestPart("image") final MultipartFile image,
                                                        @RequestParam final String name,
                                                        @RequestParam final String introduction,
                                                        @RequestParam final int budget,
                                                        @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                                                                    introduction,
                                                                    budget,
                                                                    maxParticipantSize);
        final Long createdBattleId = battleService.createProgressing(memberInfo.getId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }

    @PostMapping(path = "/completed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createCompletedBattle(@AuthenticationPrincipal final MemberInfo memberInfo,
                                                      @RequestPart("image") final MultipartFile image,
                                                      @RequestParam final String name,
                                                      @RequestParam final String introduction,
                                                      @RequestParam final int budget,
                                                      @RequestParam final int maxParticipantSize) {
        final BattleCreateRequest request = new BattleCreateRequest(name,
                                                                    introduction,
                                                                    budget,
                                                                    maxParticipantSize);
        final Long createdBattleId = battleService.createCompleted(memberInfo.getId(), image, request);
        return ResponseEntity.created(URI.create("/battles/" + createdBattleId)).build();
    }
}
