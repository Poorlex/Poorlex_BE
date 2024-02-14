package com.poorlex.poorlex.battlenotification.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.domain.BattleRepository;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.battlenotification.domain.BattleNotification;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationContent;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationImageUrl;
import com.poorlex.poorlex.battlenotification.domain.BattleNotificationRepository;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.poorlex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.poorlex.member.domain.Member;
import com.poorlex.poorlex.member.domain.MemberNickname;
import com.poorlex.poorlex.member.domain.MemberRepository;
import com.poorlex.poorlex.member.domain.Oauth2RegistrationId;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.participate.domain.BattleParticipantRepository;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import com.poorlex.poorlex.support.db.UsingDataJpaTest;
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
                softly.assertThat(battleNotification.getImageUrl())
                    .isPresent()
                    .get()
                    .isEqualTo(request.getImageUrl());
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

    @Test
    void 배틀공지를_수정한다() {
        //given
        final Battle battle = createBattle();
        final BattleParticipant manager = createManager(battle);
        final BattleNotification battleNotification = createBattleNotification(battle);
        final BattleNotificationUpdateRequest request = new BattleNotificationUpdateRequest(
            "newContentNewContent",
            "newImageUrl"
        );

        //when
        battleNotificationService.updateNotification(battleNotification.getBattleId(), manager.getMemberId(), request);

        //then
        final List<BattleNotification> battleNotifications = battleNotificationRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(battleNotifications).hasSize(1);

                final BattleNotification findBattleNotification = battleNotifications.get(0);
                softly.assertThat(findBattleNotification.getContent()).isEqualTo(request.getContent());
                softly.assertThat(findBattleNotification.getImageUrl()).contains(request.getImageUrl());
            }
        );
    }

    @Test
    void 배틀공지의_이미지를_삭제한다() {
        //given
        final Battle battle = createBattle();
        final BattleParticipant manager = createManager(battle);
        final BattleNotification battleNotification = createBattleNotification(battle);
        final BattleNotificationUpdateRequest request = new BattleNotificationUpdateRequest(
            "newContentNewContent",
            null
        );

        //when
        battleNotificationService.updateNotification(battleNotification.getBattleId(), manager.getMemberId(), request);

        //then
        final List<BattleNotification> battleNotifications = battleNotificationRepository.findAll();
        assertSoftly(
            softly -> {
                softly.assertThat(battleNotifications).hasSize(1);

                final BattleNotification findBattleNotification = battleNotifications.get(0);
                softly.assertThat(findBattleNotification.getContent()).isEqualTo(request.getContent());
                softly.assertThat(findBattleNotification.getImageUrl()).isEmpty();
            }
        );
    }

    private Battle createBattle() {
        final Battle battle = BattleFixture.simple();
        return battleRepository.save(battle);
    }

    private BattleParticipant createManager(final Battle battle) {
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        return battleParticipantRepository.save(BattleParticipant.manager(battle.getId(), member.getId()));
    }

    private BattleParticipant createNormalPlayer(final Battle battle) {
        final Member member = memberRepository.save(
            Member.withoutId(Oauth2RegistrationId.APPLE, "oauthId", new MemberNickname("nickname")));
        return battleParticipantRepository.save(BattleParticipant.normalPlayer(battle.getId(), member.getId()));
    }

    private BattleNotification createBattleNotification(final Battle battle) {
        final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
        final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
        return battleNotificationRepository.save(BattleNotification.withoutId(battle.getId(), content, imageUrl));
    }
}
