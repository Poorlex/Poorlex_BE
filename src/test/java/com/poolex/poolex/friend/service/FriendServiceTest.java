package com.poolex.poolex.friend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.friend.domain.Friend;
import com.poolex.poolex.friend.domain.FriendRepository;
import com.poolex.poolex.friend.service.dto.request.FriendCreateRequest;
import com.poolex.poolex.friend.service.dto.request.FriendDenyRequest;
import com.poolex.poolex.friend.service.dto.request.FriendInviteRequest;
import com.poolex.poolex.friend.service.dto.response.FriendResponse;
import com.poolex.poolex.friend.service.event.FriendDeniedEvent;
import com.poolex.poolex.friend.service.event.FriendInvitedEvent;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.support.IntegrationTest;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class FriendServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendService friendService;

    @Test
    void 친구를_생성한다() {
        //given
        final Long memberId = 1L;
        final FriendCreateRequest request = new FriendCreateRequest(2L);

        //when
        friendService.createFriend(memberId, request);

        //then
        final List<Friend> friends = friendRepository.findAll();
        final Friend expected = Friend.withoutId(memberId, request.getFriendMemberId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected);
    }

    @Test
    void 멤버의_친구목록을_조회한다() {
        //given
        final Member member = createMember("oauthId1", "split");
        final Member friend1 = createMember("oauthId2", "friend1");
        final Member friend2 = createMember("oauthId3", "friend2");
        final Member friend3 = createMember("oauthId4", "friend3");
        beFriend(member, friend1);
        beFriend(friend2, member);
        beFriend(friend3, member);

        //when
        final List<FriendResponse> responses = friendService.findMemberFriends(member.getId());

        //then
        final List<FriendResponse> expected = List.of(
            new FriendResponse(1, friend1.getNickname(), 0),
            new FriendResponse(1, friend2.getNickname(), 0),
            new FriendResponse(1, friend3.getNickname(), 0)
        );

        assertSoftly(
            softly -> {
                softly.assertThat(responses).hasSize(3);
                softly.assertThat(responses)
                    .usingRecursiveFieldByFieldElementComparatorOnFields()
                    .isEqualTo(expected);
            }
        );
    }

    @Test
    void 친구초대_요청을_보낸다() {
        //given
        final long memberId = 1L;
        final FriendInviteRequest request = new FriendInviteRequest(2L);

        //when
        friendService.inviteFriend(memberId, request);

        //then
        final long eventCallCount = events.stream(FriendInvitedEvent.class).count();
        assertThat(eventCallCount).isOne();
    }

    @Test
    void 친구초대_요청을_거절한다() {
        //given
        final long memberId = 1L;
        final FriendDenyRequest request = new FriendDenyRequest(2L);

        //when
        friendService.inviteDeny(memberId, request);

        //then
        final long eventCallCount = events.stream(FriendDeniedEvent.class).count();
        assertThat(eventCallCount).isOne();
    }

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(Member.withoutId(oauthId, new MemberNickname(nickname)));
    }

    private void beFriend(final Member member, final Member other) {
        friendRepository.save(Friend.withoutId(member.getId(), other.getId()));
    }
}
