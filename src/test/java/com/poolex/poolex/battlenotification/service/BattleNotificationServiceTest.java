package com.poolex.poolex.battlenotification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poolex.poolex.battle.domain.Battle;
import com.poolex.poolex.battle.domain.BattleRepository;
import com.poolex.poolex.battle.fixture.BattleFixture;
import com.poolex.poolex.battlenotification.domain.BattleNotification;
import com.poolex.poolex.battlenotification.domain.BattleNotificationRepository;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poolex.poolex.member.domain.Member;
import com.poolex.poolex.member.domain.MemberNickname;
import com.poolex.poolex.member.domain.MemberRepository;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.support.ReplaceUnderScoreTest;
import com.poolex.poolex.support.UsingDataJpaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BattleNotificationServiceTest extends UsingDataJpaTest implements ReplaceUnderScoreTest {

    @Autowired
    private BattleNotificationRepository battleNotificationRepository;

    @Autowired
    private BattleParticipantRepository battleParticipantRepository;

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private MemberRepository memberRepository;

    private BattleNotificationService battleNotificationService;

    @BeforeEach
    void setUp() {
        this.battleNotificationService =
            new BattleNotificationService(battleNotificationRepository, battleParticipantRepository);
    }

    @Test
    void 이미지URL을_포함한_배틀공지를_생성한다() {
        //given
        final Battle battle = createBattle();
        final BattleParticipant manager = createManager(battle);
        final BattleNotificationCreateRequest request =
            new BattleNotificationCreateRequest("12345678901234567890", "imageUrl");

        //when
        battleNotificationService.createNotification(battle.getId(), manager.getMemberId(), request);

        //then
        final List<BattleNotification> battleNotifications = battleNotificationRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(battleNotifications).hasSize(1);

                final BattleNotification battleNotification = battleNotifications.get(0);
                softly.assertThat(battleNotification.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleNotification.getContent()).isEqualTo(request.getContent());
                softly.assertThat(battleNotification.getImageUrl()).isPresent();
                softly.assertThat(battleNotification.getImageUrl().get()).isEqualTo(request.getImageUrl());
            }
        );
    }

    @Test
    void 이미지URL을_제외한_배틀공지를_생성한다() {
        //given
        final Battle battle = createBattle();
        final BattleParticipant manager = createManager(battle);
        final BattleNotificationCreateRequest request =
            new BattleNotificationCreateRequest("12345678901234567890", null);

        //when
        battleNotificationService.createNotification(battle.getId(), manager.getMemberId(), request);

        //then
        final List<BattleNotification> battleNotifications = battleNotificationRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(battleNotifications).hasSize(1);

                final BattleNotification battleNotification = battleNotifications.get(0);
                softly.assertThat(battleNotification.getBattleId()).isEqualTo(battle.getId());
                softly.assertThat(battleNotification.getContent()).isEqualTo(request.getContent());
                softly.assertThat(battleNotification.getImageUrl()).isEmpty();
            }
        );
    }

    @Test
    void 매니저가_아닌_참가자가_공지를_생성하면_예외를_던진다() {
        //given
        final Battle battle = createBattle();
        final BattleParticipant manager = createNormalPlayer(battle);
        final BattleNotificationCreateRequest request =
            new BattleNotificationCreateRequest("12345678901234567890", null);

        //when
        //then
        assertThatThrownBy(() -> battleNotificationService.createNotification(
                battle.getId(),
                manager.getMemberId(),
                request
            )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 해당배틀의_매니저가_아닌_참가자가_공지를_생성하면_예외를_던진다() {
        //given
        final Battle battle = createBattle();
        final Battle anotherBattle = createBattle();
        final BattleParticipant anotherBattleManager = createManager(anotherBattle);
        final BattleNotificationCreateRequest request =
            new BattleNotificationCreateRequest("12345678901234567890", null);

        //when
        //then
        assertThatThrownBy(() -> battleNotificationService.createNotification(
            battle.getId(),
            anotherBattleManager.getMemberId(),
            request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private Battle createBattle() {
        final Battle battle = BattleFixture.simple();
        return battleRepository.save(battle);
    }

    private BattleParticipant createManager(final Battle battle) {
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
        return battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));
    }

    private BattleParticipant createNormalPlayer(final Battle battle) {
        final Member member = memberRepository.save(Member.withoutId(new MemberNickname("nickname")));
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }
}
