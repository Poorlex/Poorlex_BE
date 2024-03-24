package com.poorlex.poorlex.friend.service;

import com.poorlex.poorlex.alarm.memberalram.service.MemberAlarmEventHandler;
import com.poorlex.poorlex.friend.domain.Friend;
import com.poorlex.poorlex.friend.domain.FriendRepository;
import com.poorlex.poorlex.friend.service.dto.request.FriendCreateRequest;
import com.poorlex.poorlex.friend.service.dto.response.FriendResponse;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class FriendServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendService friendService;

    @MockBean(name = "memberAlarmEventHandler")
    private MemberAlarmEventHandler memberAlarmEventHandler;

    @Test
    void 친구를_생성한다() {
        //given
        doNothing().when(memberAlarmEventHandler).friendInvitationAccepted(any());
        final Long memberId = 1L;
        final FriendCreateRequest request = new FriendCreateRequest(2L);

        //when
        friendService.inviteAccept(memberId, request);

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
    void 친구를_생성한다_이미_친구일_때() {
        //given
        doNothing().when(memberAlarmEventHandler).friendInvitationAccepted(any());
        final Long memberId = 1L;
        final FriendCreateRequest request = new FriendCreateRequest(2L);
        friendRepository.save(Friend.withoutId(memberId, request.getFriendMemberId()));

        //when
        //then
        assertThatThrownBy(() -> friendService.inviteAccept(memberId, request))
                .isInstanceOf(IllegalArgumentException.class);
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
        final List<FriendResponse> responses = friendService.findMemberFriendsWithCurrentDateTime(member.getId());

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

    private Member createMember(final String oauthId, final String nickname) {
        return memberRepository.save(
                Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname(nickname)));
    }

    private void beFriend(final Member member, final Member other) {
        friendRepository.save(Friend.withoutId(member.getId(), other.getId()));
    }
}
