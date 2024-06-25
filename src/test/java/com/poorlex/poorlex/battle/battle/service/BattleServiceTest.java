package com.poorlex.poorlex.battle.battle.service;

import com.poorlex.poorlex.battle.battle.domain.*;
import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleFindRequest;
import com.poorlex.poorlex.battle.battle.service.dto.request.BattleUpdateRequest;
import com.poorlex.poorlex.battle.battle.service.dto.response.*;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRole;
import com.poorlex.poorlex.battle.participation.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.battle.participation.service.event.BattlesWithdrewEvent;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.exception.BadRequestException;
import com.poorlex.poorlex.exception.ExceptionTag;
import com.poorlex.poorlex.exception.ForbiddenException;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("배틀 서비스 테스트")
class BattleServiceTest extends IntegrationTest implements ReplaceUnderScoreTest {

    private static final LocalDateTime BATTLE_START_DATE = LocalDateTime.of(
            LocalDate.of(2023, 12, 25),
            LocalTime.of(0, 20)
    );
    private static final LocalDateTime BATTLE_END_TIME = LocalDateTime.of(
            LocalDate.of(2023, 12, 31),
            LocalTime.of(23, 59)
    );
    private static final BattleDuration BATTLE_DURATION = new BattleDuration(BATTLE_START_DATE, BATTLE_END_TIME);

    @Autowired
    private BattleService battleService;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @MockBean
    private BattleImageService imageService;

    @MockBean
    private BattleParticipantChangedEventHandler battleParticipantChangedEventHandler;

    @BeforeEach
    void setUp() {
        given(imageService.saveAndReturnPath(any(), any())).willReturn(BattleFixture.simple().getImageUrl());
        doNothing().when(battleParticipantChangedEventHandler).added(any(BattleParticipantAddedEvent.class));
        doNothing().when(battleParticipantChangedEventHandler).added(any(BattlesWithdrewEvent.class));
    }

    @Test
    void 배틀을_생성한다() throws IOException {
        //given
        final Battle excpectedBattle = BattleFixture.simple();
        final long createMemberId = 1L;
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        battleService.create(createMemberId, image, BattleFixture.request());

        //then
        final List<Battle> battles = battleRepository.findAll();
        assertSoftly(
                (softly) -> {
                    softly.assertThat(battles).hasSize(1);
                    softly.assertThat(battles.get(0)).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt")
                            .isEqualTo(excpectedBattle);
                }
        );
    }

    @ParameterizedTest(name = "멤버가 해당 배틀에 {1} 때 현재 참여 멤버는 {3}")
    @CsvSource(
            value = {"true:참여중일:2", "false:참여중이 아닐:1"},
            delimiter = ':'
    )
    void 배틀의_상세_정보를_조회한다(Boolean participating, String 참여여부, Integer currentParticipantSize) {
        // given
        final Member member = createMemberWithOauthId("oauthId1");
        final Member manager = createMemberWithOauthId("manager-oauthId");
        final Battle battle = createBattleWithManager(manager, 10000, BattleStatus.PROGRESS, BATTLE_DURATION);

        if (participating)
            join(member, battle);

        // when
        BattleResponse battleInfo = battleService.getBattleInfo(member.getId(), battle.getId());

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(battleInfo.getId()).isEqualTo(battle.getId());
                    softly.assertThat(battleInfo.getBattleName()).isEqualTo(battle.getName());
                    softly.assertThat(battleInfo.getBattleImageUrl()).isEqualTo(battle.getImageUrl());
                    softly.assertThat(battleInfo.getMaxParticipantSize()).isEqualTo(battle.getMaxParticipantSize().getValue());
                    softly.assertThat(battleInfo.getCurrentParticipantSize()).isEqualTo(currentParticipantSize);
                    softly.assertThat(battleInfo.getBattleBudget()).isEqualTo(battle.getBudget());
                    softly.assertThat(battleInfo.getBattleIntroduction()).isEqualTo(battle.getIntroduction());
                    softly.assertThat(battleInfo.getIsParticipating()).isEqualTo(participating);
                    softly.assertThat(battleInfo.getBattleManager()).isNotNull();
                    softly.assertThat(battleInfo.getBattleManager().nickname()).isEqualTo(manager.getNickname());
                    softly.assertThat(battleInfo.getBattleManager().level()).isEqualTo(1);
                }
        );
    }

    @ParameterizedTest(name = "멤버1의 지출이 {0}, 멤버2의 지출이 {1} 일 때")
    @CsvSource(
            value = {"1000:1000:1", "1000:2000:1", "2000:1000:2"},
            delimiter = ':'
    )
    void 멤버가_포함된_진행중인_배틀들의_정보를_조회한다(final Long member1Expenditure,
                                    final Long member2Expenditure,
                                    final int expectedRank) {
        //given
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Battle battle = createSimpleBattleWithStatusAndDuration(10000, BattleStatus.PROGRESS, BATTLE_DURATION);

        join(member1, battle);
        join(member2, battle);

        expend(member1Expenditure, member1, LocalDate.from(BATTLE_START_DATE));
        expend(member2Expenditure, member2, LocalDate.from(BATTLE_START_DATE));

        //when
        final List<MemberProgressBattleResponse> battles =
                battleService.findProgressMemberBattles(member1.getId(), LocalDate.from(BATTLE_START_DATE));

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(battles).hasSize(1);
                    softly.assertThat(battles.get(0).getDDay()).isEqualTo(6);
                    softly.assertThat(battles.get(0).getBudgetLeft()).isEqualTo(10000 - member1Expenditure);
                    softly.assertThat(battles.get(0).getCurrentParticipantRank()).isEqualTo(expectedRank);
                }
        );
    }

    @ParameterizedTest(name = "멤버1의 지출이 {0}, 멤버2의 지출이 {1} 일 때")
    @CsvSource(
            value = {"7000:14000:1", "7000:21000:1", "14000:7000:2"},
            delimiter = ':'
    )
    void 멤버가_포함되어있는_종료된_배틀들의_정보를_조회한다(final Long member1Expenditure,
                                      final Long member2Expenditure,
                                      final int expectedRank) {
        //given
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Battle battle = createSimpleBattleWithStatusAndDuration(10000, BattleStatus.COMPLETE, BATTLE_DURATION);

        join(member1, battle);
        join(member2, battle);

        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(1));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(2));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(3));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(4));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(5));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(6));

        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(1));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(2));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(3));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(4));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(5));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(6));

        //when
        final List<MemberCompleteBattleResponse> battles =
                battleService.findCompleteMemberBattles(member1.getId(), LocalDate.from(BATTLE_END_TIME).plusDays(1));

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(battles).hasSize(1);
                    softly.assertThat(battles.get(0).getPastDay()).isEqualTo(1);
                    softly.assertThat(battles.get(0).getCurrentParticipantRank()).isEqualTo(expectedRank);
                    softly.assertThat(battles.get(0).getBudgetLeft()).isEqualTo(10000 - member1Expenditure);
                    softly.assertThat(battles.get(0).getBudget()).isEqualTo(10000);
                    softly.assertThat(battles.get(0).getEarnedPoint())
                            .isEqualTo(BattleType.LARGE.getScore(expectedRank));
                }
        );
    }

    @ParameterizedTest(name = "배틀 상태가 {0} 일 때")
    @CsvSource(
            value = {"RECRUITING", "PROGRESS", "COMPLETE"}
    )
    void 쿼리에_따라_다른_상태의_배틀들의_정보를_조회한다(final String status) {
        //given
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Member member3 = createMemberWithOauthId("oauthId3");
        final Battle battle1 = createSimpleBattleWithStatusAndDuration(30000, BattleStatus.RECRUITING, BATTLE_DURATION);
        final Battle battle2 = createSimpleBattleWithStatusAndDuration(60000, BattleStatus.RECRUITING, BATTLE_DURATION);
        final Battle battle3 = createSimpleBattleWithStatusAndDuration(90000, BattleStatus.PROGRESS, BATTLE_DURATION);
        final Battle battle4 = createSimpleBattleWithStatusAndDuration(120000, BattleStatus.PROGRESS, BATTLE_DURATION);
        final Battle battle5 = createSimpleBattleWithStatusAndDuration(150000, BattleStatus.PROGRESS, BATTLE_DURATION);
        final Battle battle6 = createSimpleBattleWithStatusAndDuration(180000, BattleStatus.COMPLETE, BATTLE_DURATION);
        final Battle battle7 = createSimpleBattleWithStatusAndDuration(200000, BattleStatus.COMPLETE, BATTLE_DURATION);

        Map<Long, Integer> currentParticipant = new HashMap<>();
        join(member1, battle1);
        join(member2, battle1);
        join(member3, battle1);
        currentParticipant.put(battle1.getId(), 3);

        join(member1, battle2);
        join(member3, battle2);
        currentParticipant.put(battle2.getId(), 2);

        join(member1, battle3);
        currentParticipant.put(battle3.getId(), 1);

        join(member1, battle4);
        join(member3, battle4);
        currentParticipant.put(battle4.getId(), 2);

        join(member1, battle5);
        join(member2, battle5);
        currentParticipant.put(battle5.getId(), 2);

        join(member3, battle6);
        currentParticipant.put(battle6.getId(), 1);

        join(member1, battle7);
        join(member2, battle7);
        join(member3, battle7);
        currentParticipant.put(battle7.getId(), 3);

        List<Battle> battlesInRecruiting = List.of(battle1, battle2);
        List<Battle> battlesInProgress = List.of(battle3, battle4, battle5);
        List<Battle> battlesInComplete = List.of(battle6, battle7);

        Map<String, List<Battle>> result = new HashMap<>();
        result.put("RECRUITING", battlesInRecruiting);
        result.put("PROGRESS", battlesInProgress);
        result.put("COMPLETE", battlesInComplete);

        List<Long> battleId = result.get(status).stream().map(Battle::getId).toList();
        List<String> name = result.get(status).stream().map(Battle::getName).toList();
        List<String> introduction = result.get(status).stream().map(Battle::getIntroduction).toList();
        List<String> imageUrl = result.get(status).stream().map(Battle::getImageUrl).toList();
        List<Integer> budget = result.get(status).stream().map(Battle::getBudget).toList();
        List<String> difficulty = result.get(status).stream().map(b -> b.getDifficulty().name()).toList();
        List<Integer> participantSize = result.get(status).stream().map(b -> b.getMaxParticipantSize().getValue()).toList();

        //when
        BattleFindRequest battleFindRequest = new BattleFindRequest(null, List.of(BattleStatus.valueOf(status)));

        final List<FindingBattleResponse> battles = battleService.queryBattles(battleFindRequest, Pageable.ofSize(20));

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(battles).hasSize(result.get(status).size());
                    softly.assertThat(battles).extracting("battleId").containsExactlyInAnyOrderElementsOf(battleId);
                    softly.assertThat(battles).extracting("name").containsExactlyInAnyOrderElementsOf(name);
                    softly.assertThat(battles).extracting("introduction").containsExactlyInAnyOrderElementsOf(introduction);
                    softly.assertThat(battles).extracting("imageUrl").containsExactlyInAnyOrderElementsOf(imageUrl);
                    softly.assertThat(battles).extracting("budget").containsExactlyInAnyOrderElementsOf(budget);
                    softly.assertThat(battles).extracting("difficulty").containsExactlyInAnyOrderElementsOf(difficulty);
                    softly.assertThat(battles).extracting("maxParticipantCount").containsExactlyInAnyOrderElementsOf(participantSize);
                    softly.assertThat(battles).allMatch(b -> b.getCurrentParticipant() == currentParticipant.get(b.getBattleId()));
                }
        );
    }

    @Transactional
    @ParameterizedTest(name = "멤버1의 지출이 {0}, 멤버2의 지출이 {1} 일 때")
    @CsvSource(
            value = {"7000:14000:1:2", "7000:21000:1:2", "14000:7000:1:2"},
            delimiter = ':'
    )
    void 배틀에_참여한_멤버의_랭킹_정보를_조회한다(Long member1Expenditure, Long member2Expenditure, int member1ExpectedRank, int member2ExpectedRank) {
        final Member member1 = createMemberWithOauthId("oauthId1");
        final Member member2 = createMemberWithOauthId("oauthId2");
        final Battle battle = createBattleWithManager(member1, 10000, BattleStatus.COMPLETE, BATTLE_DURATION);

        join(member2, battle);

        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(1));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(2));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(3));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(4));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(5));
        expend(member1Expenditure / 7, member1, LocalDate.from(BATTLE_START_DATE).plusDays(6));

        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(1));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(2));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(3));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(4));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(5));
        expend(member2Expenditure / 7, member2, LocalDate.from(BATTLE_START_DATE).plusDays(6));


        List<ParticipantRankingResponse> participantsRankings = battleService.getParticipantsRankings(member1.getId());

        //then
        assertSoftly(
                softly -> {
                    softly.assertThat(participantsRankings).hasSize(2);
                    softly.assertThat(participantsRankings).extracting("rank")
                            .containsExactlyInAnyOrder(member1ExpectedRank, member2ExpectedRank);
                    softly.assertThat(participantsRankings).extracting("nickname")
                            .containsExactlyInAnyOrder("nickname", "nickname");
                    softly.assertThat(participantsRankings).extracting("expenditure")
                            .containsExactlyInAnyOrder(member1Expenditure, member2Expenditure);
                    softly.assertThat(participantsRankings).extracting("role")
                            .containsExactlyInAnyOrder(BattleParticipantRole.MANAGER.name(), BattleParticipantRole.NORMAL_PLAYER.name());
                }
        );
    }

    @Test
    @Transactional
    void 배틀을_삭제한다() {
        //given
        final Member manager = createMemberWithOauthId("oauthId");
        final Battle battle = createBattleWithManager(manager, 30000, BattleStatus.RECRUITING, BATTLE_DURATION);

        //when
        battleService.delete(manager.getId(), battle.getId());

        //then
        assertSoftly(softly -> softly
                .assertThat(battleRepository.findById(battle.getId())).isEmpty());
    }

    @Test
    @Transactional
    void 일반_참가자가_배틀을_삭제하려고_하면_ForbiddenException_예외() {
        //given
        final Member manager = createMemberWithOauthId("oauthId1");
        final Member member = createMemberWithOauthId("oauthId2");
        final Battle battle = createBattleWithManager(manager, 30000, BattleStatus.RECRUITING, BATTLE_DURATION);
        join(member, battle);

        //when, then
        assertSoftly(softly -> softly.assertThatThrownBy(() -> battleService.delete(member.getId(), battle.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("배틀 변경/삭제는 매니저만 가능합니다."));
    }

    @Test
    @Transactional
    void 배틀_정보를_수정한다() throws IOException {
        //given
        final Member manager = createMemberWithOauthId("oauthId1");
        final Battle battle = createBattleWithManager(manager, 30000, BattleStatus.RECRUITING, BATTLE_DURATION);
        BattleUpdateRequest request = new BattleUpdateRequest("변경된 이름", "변경된 소개문");
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when
        battleService.updateBattle(manager.getId(), battle.getId(), image, request);
        Battle updatedBattle = battleRepository.findById(battle.getId())
                .orElseThrow(() -> new BadRequestException(ExceptionTag.BATTLE_FIND, "배틀을 찾을 수 없습니다."));

        //then
        assertSoftly(softly -> {
                    softly.assertThat(updatedBattle.getName()).isEqualTo(request.name());
                    softly.assertThat(updatedBattle.getIntroduction()).isEqualTo(request.introduction());
                });
    }

    @Test
    @Transactional
    void 일반_참가자가_배틀_정보를_수정하려고_하면_ForbiddenException_예외() throws IOException {
        //given
        final Member manager = createMemberWithOauthId("oauthId1");
        final Member member = createMemberWithOauthId("oauthId2");
        final Battle battle = createBattleWithManager(manager, 30000, BattleStatus.RECRUITING, BATTLE_DURATION);
        join(member, battle);
        BattleUpdateRequest request = new BattleUpdateRequest("변경된 이름", "변경된 소개문");
        final MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat-8415620_640",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(
                        "src/test/resources/testImage/cat-8415620_640.jpg")
        );

        //when, then
        assertSoftly(softly -> softly.assertThatThrownBy(() -> battleService.updateBattle(member.getId(), battle.getId(), image, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("배틀 변경/삭제는 매니저만 가능합니다."));
    }

    private Member createMemberWithOauthId(final String oauthId) {
        final Member member = Member.withoutId(Oauth2RegistrationId.APPLE, oauthId, new MemberNickname("nickname"));
        return memberRepository.save(member);
    }

    private void join(final Member member, final Battle battle) {
        memberRepository.save(member);
        final BattleParticipant battleParticipant = BattleParticipant.normalPlayer(battle.getId(), member.getId());
        battleParticipantRepository.save(battleParticipant);
    }

    private Battle createSimpleBattleWithStatusAndDuration(final int budget,
                                                           final BattleStatus status,
                                                           final BattleDuration battleDuration) {
        final Battle battle = BattleFixture.initialBattleBuilder()
                .budget(new BattleBudget(budget))
                .status(status)
                .duration(battleDuration)
                .build();

        return battleRepository.save(battle);
    }

    private Battle createBattleWithManager(Member member, final int budget,
                                                           final BattleStatus status,
                                                           final BattleDuration battleDuration) {
        Battle battle = battleRepository.save(BattleFixture.initialBattleBuilder()
                .budget(new BattleBudget(budget))
                .status(status)
                .duration(battleDuration)
                .build());

        battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));

        return battle;
    }

    private void expend(final Long amount, final Member member, final LocalDate date) {
        expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, member.getId(), date));
    }
}
