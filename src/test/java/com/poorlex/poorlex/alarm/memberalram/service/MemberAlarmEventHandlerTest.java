package com.poorlex.poorlex.alarm.memberalram.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarm;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmRepository;
import com.poorlex.poorlex.alarm.memberalram.domain.MemberAlarmType;
import com.poorlex.poorlex.friend.service.event.FriendAcceptedEvent;
import com.poorlex.poorlex.friend.service.event.FriendDeniedEvent;
import com.poorlex.poorlex.friend.service.event.FriendInvitedEvent;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberAlarmEventHandlerTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private MemberAlarmRepository memberAlarmRepository;
    private MemberAlarmEventHandler memberAlarmEventHandler;

    @BeforeEach
    void setUp() {
        this.memberAlarmEventHandler = new MemberAlarmEventHandler(memberAlarmRepository);
    }

    @Test
    void 친구_초대_이벤트_발생시_친구_초대_알림을_생성한다() {
        //given
        final FriendInvitedEvent friendInvitedEvent = FriendInvitedEvent.builder()
            .inviteMemberId(1L)
            .invitedMemberId(2L)
            .build();

        //when
        memberAlarmEventHandler.friendInvitation(friendInvitedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(friendInvitedEvent.getInvitedMemberId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(friendInvitedEvent.getInviteMemberId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_INVITATION);
            }
        );
    }

    @Test
    void 친구_수락_이벤트_발생시_친구_수락_알림을_생성한다() {
        //given
        final FriendAcceptedEvent friendAcceptedEvent = FriendAcceptedEvent.builder()
            .inviteMemberId(1L)
            .acceptMemberId(2L)
            .build();

        //when
        memberAlarmEventHandler.friendInvitationAccepted(friendAcceptedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(friendAcceptedEvent.getInviteMemberId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(friendAcceptedEvent.getAcceptMemberId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_ACCEPTED);
            }
        );
    }

    @Test
    void 친구_거절_이벤트_발생시_친구_거절_알림을_생성한다() {
        //given
        final FriendDeniedEvent friendDeniedEvent = FriendDeniedEvent.builder()
            .inviteMemberId(1L)
            .denyMemberId(2L)
            .build();

        //when
        memberAlarmEventHandler.friendInvitationDenied(friendDeniedEvent);

        //then
        final List<MemberAlarm> memberAlarms = memberAlarmRepository.findAll();

        assertSoftly(
            softly -> {
                softly.assertThat(memberAlarms).hasSize(1);

                final MemberAlarm memberAlarm = memberAlarms.get(0);
                softly.assertThat(memberAlarm.getMemberId()).isEqualTo(friendDeniedEvent.getInviteMemberId());
                softly.assertThat(memberAlarm.getTargetId()).isEqualTo(friendDeniedEvent.getDenyMemberId());
                softly.assertThat(memberAlarm.getType()).isEqualTo(MemberAlarmType.FRIEND_DENIED);
            }
        );
    }
}
