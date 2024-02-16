package com.poorlex.poorlex.alarm.battlealarm.service;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarm;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmRepository;
import com.poorlex.poorlex.alarm.battlealarm.domain.BattleAlarmType;
import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battlenotification.domain.BattleNotification;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationContent;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationRepository;
import com.poorlex.poorlex.battlenotification.service.BattleNotificationService;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.battlenotification.service.event.BattleNotificationChangedEvent;
import com.poorlex.poorlex.config.aws.AWSS3Service;
import com.poorlex.poorlex.expenditure.service.ExpenditureService;
import com.poorlex.poorlex.expenditure.service.dto.request.ExpenditureCreateRequest;
import com.poorlex.poorlex.expenditure.service.event.ExpenditureCreatedEvent;
import com.poorlex.poorlex.expenditure.service.event.ZeroExpenditureCreatedEvent;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.SpringEventTest;
import com.poorlex.poorlex.voting.vote.domain.Vote;
import com.poorlex.poorlex.voting.vote.domain.VoteAmount;
import com.poorlex.poorlex.voting.vote.domain.VoteDuration;
import com.poorlex.poorlex.voting.vote.domain.VoteDurationType;
import com.poorlex.poorlex.voting.vote.domain.VoteName;
import com.poorlex.poorlex.voting.vote.domain.VoteRepository;
import com.poorlex.poorlex.voting.vote.domain.VoteStatus;
import com.poorlex.poorlex.voting.vote.service.VoteService;
import com.poorlex.poorlex.voting.vote.service.dto.request.VoteCreateRequest;
import com.poorlex.poorlex.voting.vote.service.event.VoteCreatedEvent;
import com.poorlex.poorlex.voting.votingpaper.service.VotingPaperService;
import com.poorlex.poorlex.voting.votingpaper.service.dto.request.VotingPaperCreateRequest;
import com.poorlex.poorlex.voting.votingpaper.service.event.VotingPaperCreatedEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class BattleAlarmEventHandlerTest extends SpringEventTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleAlarmRepository battleAlarmRepository;

    @Autowired
    private BattleAlarmEventHandler battleAlarmEventHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private BattleNotificationService battleNotificationService;

    @Autowired
    private ExpenditureService expenditureService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingPaperService votingPaperService;

    @MockBean
    private AWSS3Service awss3Service;

    @Test
    void 공지가_생성되면_공지_변경_이벤트를_처리한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        joinAsManager(battle, member);

        //when
        battleNotificationService.createNotification(
            battle.getId(),
            member.getId(),
            new BattleNotificationCreateRequest("notificationContentNotificationContent", "imageUrl")
        );

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(BattleNotificationChangedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();
                softly.assertThat(battleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = battleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 공지가_변경되면_공지_변경_이벤트를_처리한다() {
        //given
        final Battle battle = createBattle();
        final Member member = createMember("oauthId");
        joinAsManager(battle, member);
        createBattleNotification(battle);

        //when
        battleNotificationService.updateNotification(
            battle.getId(),
            member.getId(),
            new BattleNotificationUpdateRequest("notificationContentNotificationContent", "imageUrl")
        );

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(BattleNotificationChangedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();
                softly.assertThat(battleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = battleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 공지_변경_이벤트를_처리한다() {
        //given
        final BattleNotificationChangedEvent event = new BattleNotificationChangedEvent(1L, 1L);

        //when
        battleAlarmEventHandler.notificationChangedAlarm(event);

        //then
        final List<BattleAlarm> battleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
        assertSoftly(
            softly -> {
                softly.assertThat(battleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = battleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.BATTLE_NOTIFICATION_CHANGED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(1L);
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(1L);
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(
                        LocalDateTime.now().truncatedTo(ChronoUnit.MICROS).truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 지출이_등록되면_지출_생성_이벤트를_처리한다() throws IOException {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("imageUrl");
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsManager(battle, member);

        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            1000,
            "description",
            battle.getDuration().getStart()
        );

        final MockMultipartFile file = new MockMultipartFile(
            "file",
            "cat-8415620_640",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new FileInputStream(
                "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        expenditureService.createExpenditure(member.getId(), List.of(file), request);

        //then
        final List<BattleAlarm> expenditureCreatedBattleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.EXPENDITURE_CREATED);
        final List<BattleAlarm> overBudgetBattleAlarms = battleAlarmRepository.findByType(BattleAlarmType.OVER_BUDGET);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(ExpenditureCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(expenditureCreatedBattleAlarms).hasSize(1);
                softly.assertThat(overBudgetBattleAlarms).isEmpty();

                final BattleAlarm battleAlarm = expenditureCreatedBattleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.EXPENDITURE_CREATED);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 금액이_0인_지출이_등록되면_무지출_이벤트를_처리한다() throws IOException {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("imageUrl");
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsManager(battle, member);

        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            0,
            "description",
            battle.getDuration().getStart()
        );

        //when
        final MockMultipartFile file = new MockMultipartFile(
            "file",
            "cat-8415620_640",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new FileInputStream(
                "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        expenditureService.createExpenditure(member.getId(), List.of(file), request);

        //then
        final List<BattleAlarm> zeroExpenditureAlarms =
            battleAlarmRepository.findByType(BattleAlarmType.ZERO_EXPENDITURE);

        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(ZeroExpenditureCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(zeroExpenditureAlarms).hasSize(1);
                final BattleAlarm battleAlarm = zeroExpenditureAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.ZERO_EXPENDITURE);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 지출_생성_이벤트를_처리할_때_배틀의_예산보다_주간_지출이_클_경우_지출_초과_알림을_생성한다() throws IOException {
        //given
        given(awss3Service.uploadMultipartFile(any(), any())).willReturn("imageUrl");
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsManager(battle, member);

        final ExpenditureCreateRequest request = new ExpenditureCreateRequest(
            11000,
            "description",
            battle.getDuration().getStart()
        );

        final MockMultipartFile file = new MockMultipartFile(
            "file",
            "cat-8415620_640",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new FileInputStream(
                "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        expenditureService.createExpenditure(member.getId(), List.of(file), request);

        //then
        final List<BattleAlarm> expenditureCreatedBattleAlarms = battleAlarmRepository.findByType(
            BattleAlarmType.EXPENDITURE_CREATED);
        final List<BattleAlarm> overBudgetBattleAlarms = battleAlarmRepository.findByType(BattleAlarmType.OVER_BUDGET);
        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(ExpenditureCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(expenditureCreatedBattleAlarms).hasSize(1);
                softly.assertThat(overBudgetBattleAlarms).hasSize(1);

                final BattleAlarm battleAlarm = overBudgetBattleAlarms.get(0);
                softly.assertThat(battleAlarm.getType()).isEqualTo(BattleAlarmType.OVER_BUDGET);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 투표가_생성되면_투표_생성_이벤트를_처리한다() {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        joinAsPlayer(battle, member);

        //when
        final VoteCreateRequest request = new VoteCreateRequest(1000, LocalDateTime.now(), 10, "초코우유");
        voteService.createVote(member.getId(), battle.getId(), request);

        //then
        final List<BattleAlarm> voteCreatedAlarm = battleAlarmRepository.findByType(BattleAlarmType.VOTE_CREATED);

        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(VoteCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(voteCreatedAlarm).hasSize(1);

                final BattleAlarm battleAlarm = voteCreatedAlarm.get(0);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    @Test
    void 투표표가_생성되면_투표표_생성_이벤트를_처리한다() {
        //given
        final Member member = createMember("oauthId");
        final Battle battle = createBattle(BattleStatus.PROGRESS);
        final Vote vote = createVote(member.getId(), battle.getId());
        joinAsPlayer(battle, member);

        //when
        final VotingPaperCreateRequest request = new VotingPaperCreateRequest(true);
        votingPaperService.createVotingPaper(member.getId(), vote.getId(), request);

        //then
        final List<BattleAlarm> votingPaperCreatedAlarms =
            battleAlarmRepository.findByType(BattleAlarmType.VOTING_PAPER_CREATED);

        assertSoftly(
            softly -> {
                final long eventListenCount = events.stream(VotingPaperCreatedEvent.class).count();
                softly.assertThat(eventListenCount).isOne();

                softly.assertThat(votingPaperCreatedAlarms).hasSize(1);

                final BattleAlarm battleAlarm = votingPaperCreatedAlarms.get(0);
                softly.assertThat(battleAlarm.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleAlarm.getMemberId()).isEqualTo(member.getId());
                softly.assertThat(battleAlarm.getCreatedAt())
                    .isBeforeOrEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            }
        );
    }

    private Vote createVote(final Long memberId, final Long battleId) {
        return voteRepository.save(
            Vote.withoutId(
                battleId,
                memberId,
                new VoteAmount(1000),
                new VoteDuration(LocalDateTime.now(), VoteDurationType.TEN_MINUTE),
                new VoteName("초코우유"),
                VoteStatus.PROGRESS
            )
        );
    }

    private Member createMember(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private Battle createBattle() {
        return battleRepository.save(BattleFixture.simple());
    }

    private Battle createBattle(final BattleStatus battleStatus) {
        return battleRepository.save(BattleFixture.initialBattleBuilder().status(battleStatus).build());
    }

    private void joinAsManager(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));
    }

    private void joinAsPlayer(final Battle battle, final Member member) {
        battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }

    private void createBattleNotification(final Battle battle) {
        battleNotificationRepository.save(BattleNotification.withoutIdAndImageUrl(
            battle.getId(),
            new BattleNotificationContent("ThisIsBattleNotificationContent"))
        );
    }
}
