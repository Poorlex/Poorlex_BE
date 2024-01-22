package com.poorlex.poorlex.friend.controller;

import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberOnly;
import com.poorlex.poorlex.friend.service.FriendService;
import com.poorlex.poorlex.friend.service.dto.request.FriendCreateRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendDenyRequest;
import com.poorlex.poorlex.friend.service.dto.request.FriendInviteRequest;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        final List<FriendResponse> responses = friendService.findMemberFriends(memberInfo.getMemberId());
        return ResponseEntity.ok(responses);
    }
}
