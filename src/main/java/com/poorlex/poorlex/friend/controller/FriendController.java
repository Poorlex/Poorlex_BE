package com.poorlex.poorlex.friend.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.*;
import com.poorlex.poorlex.friend.service.*;
import com.poorlex.poorlex.friend.service.dto.request.*;
import com.poorlex.poorlex.friend.service.dto.response.*;
import java.util.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteFriend(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final FriendInviteRequest request) {
        friendService.inviteFriend(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<Void> inviteAccept(@MemberOnly final MemberInfo memberInfo,
                                             @RequestBody final FriendCreateRequest request) {
        friendService.inviteAccept(memberInfo.getMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/invite/deny")
    public ResponseEntity<Void> inviteDeny(@MemberOnly final MemberInfo memberInfo,
                                           @RequestBody final FriendDenyRequest request) {
        friendService.inviteDeny(memberInfo.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> findMemberFriends(@MemberOnly final MemberInfo memberInfo) {
        final List<FriendResponse> responses = friendService.findMemberFriendsWithCurrentDateTime(memberInfo.getMemberId());
        return ResponseEntity.ok(responses);
    }
}
