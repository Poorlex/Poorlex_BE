package com.poorlex.poorlex.battle.battle.service;

import com.poorlex.poorlex.battle.battle.domain.Battle;
import com.poorlex.poorlex.battle.battle.domain.BattleBudget;
import com.poorlex.poorlex.battle.battle.domain.BattleDuration;
import com.poorlex.poorlex.battle.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.battle.domain.BattleStatus;
import com.poorlex.poorlex.battle.battle.domain.BattleType;
import com.poorlex.poorlex.battle.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberCompleteBattleResponse;
import com.poorlex.poorlex.battle.battle.service.dto.response.MemberProgressBattleResponse;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipant;
import com.poorlex.poorlex.battle.participation.domain.BattleParticipantRepository;
import com.poorlex.poorlex.battle.participation.service.event.BattleParticipantAddedEvent;
import com.poorlex.poorlex.battle.participation.service.event.BattlesWithdrewEvent;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.consumption.expenditure.fixture.ExpenditureFixture;
import com.poorlex.poorlex.support.IntegrationTest;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.user.member.domain.Member;
import com.poorlex.poorlex.user.member.domain.MemberNickname;
import com.poorlex.poorlex.user.member.domain.MemberRepository;
import com.poorlex.poorlex.user.member.domain.Oauth2RegistrationId;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

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
                    softly.assertThat(battles.get(0).getEarnedPoint())
                            .isEqualTo(BattleType.LARGE.getScore(expectedRank));
                }
        );
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

    private void expend(final Long amount, final Member member, final LocalDate date) {
        expenditureRepository.save(ExpenditureFixture.simpleWithMainImage(amount, member.getId(), date));
    }
}