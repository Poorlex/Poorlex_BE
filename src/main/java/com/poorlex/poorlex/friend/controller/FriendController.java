package com.poorlex.poorlex.friend.controller;

import com.poorlex.poorlex.friend.service.*;
import com.poorlex.poorlex.friend.service.dto.request.*;
import com.poorlex.poorlex.friend.service.dto.response.*;
import java.util.*;
import com.poorlex.poorlex.security.service.MemberInfo;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteFriend(@AuthenticationPrincipal final MemberInfo memberInfo,
                                             @RequestBody final FriendInviteRequest request) {
        friendService.inviteFriend(memberInfo.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<Void> inviteAccept(@AuthenticationPrincipal final MemberInfo memberInfo,
                                             @RequestBody final FriendCreateRequest request) {
        friendService.inviteAccept(memberInfo.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/invite/deny")
    public ResponseEntity<Void> inviteDeny(@AuthenticationPrincipal final MemberInfo memberInfo,
                                           @RequestBody final FriendDenyRequest request) {
        friendService.inviteDeny(memberInfo.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> findMemberFriends(@AuthenticationPrincipal final MemberInfo memberInfo) {
        final List<FriendResponse> responses = friendService.findMemberFriendsWithCurrentDateTime(memberInfo.getId());
        return ResponseEntity.ok(responses);
    }
}
