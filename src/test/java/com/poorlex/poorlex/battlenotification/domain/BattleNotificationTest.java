package com.poorlex.poorlex.battlenotification.domain;

import com.poorlex.poorlex.battle.domain.Battle;
import com.poorlex.poorlex.battle.fixture.BattleFixture;
import com.poorlex.poorlex.participate.domain.BattleParticipant;
import com.poorlex.poorlex.support.ReplaceUnderScoreTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("배틀 공지 테스트")
class BattleNotificationTest implements ReplaceUnderScoreTest {

    @DisplayName("배틀 공지 내용 변경 테스트")
    @Nested
    class ChangeContent {

        @Test
        void 배틀_공지의_내용을_변경한다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            //when
            final BattleNotificationContent newContent = new BattleNotificationContent("new12345678901234567890");
            final BattleParticipant manager = BattleParticipant.manager(battle.getId(), 1L);
            battleNotification.changeContent(manager, newContent);

            //then
            assertThat(battleNotification.getContent()).isEqualTo(newContent.getValue());
        }

        @Test
        void 배틀_공지의_내용을_매니저가_아닌_참가자가_변경하면_에외를_던진다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            final BattleNotificationContent newContent = new BattleNotificationContent("new12345678901234567890");
            final BattleParticipant normalPlayer = BattleParticipant.normalPlayer(battle.getId(), 1L);

            //when
            //then
            assertThatThrownBy(() -> battleNotification.changeContent(normalPlayer, newContent))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 배틀_공지의_내용을_다른_배틀참가자가_변경하면_에외를_던진다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final long anotherBattleId = 2L;
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            final BattleNotificationContent newContent = new BattleNotificationContent("new12345678901234567890");
            final BattleParticipant normalPlayer = BattleParticipant.manager(anotherBattleId, 1L);

            //when
            //then
            assertThatThrownBy(() -> battleNotification.changeContent(normalPlayer, newContent))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("배틀 이미지 변경 테스트")
    @Nested
    class ChangeImageUrl {

        @Test
        void 배틀_이미지URL을_변경한다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            //when
            final BattleNotificationImageUrl newImageUrl = new BattleNotificationImageUrl("newImageUrl");
            final BattleParticipant manager = BattleParticipant.manager(battle.getId(), 1L);
            battleNotification.changeImage(manager, newImageUrl);

            //then
            assertThat(battleNotification.getImageUrl()).isPresent();
            assertThat(battleNotification.getImageUrl()).contains(newImageUrl.getValue());
        }

        @Test
        void 배틀_이미지URL을_매니저가_아닌_참가자가_변경하면_에외를_던진다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            final BattleNotificationImageUrl newImageUrl = new BattleNotificationImageUrl("newImageUrl");
            final BattleParticipant normalPlayer = BattleParticipant.normalPlayer(battle.getId(), 1L);

            //when
            //then
            assertThatThrownBy(() -> battleNotification.changeImage(normalPlayer, newImageUrl))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 배틀_이미지URL을_다른_배틀참가자가_변경하면_에외를_던진다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final long anotherBattleId = 2L;
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            final BattleNotificationImageUrl newImageUrl = new BattleNotificationImageUrl("newImageUrl");
            final BattleParticipant normalPlayer = BattleParticipant.manager(anotherBattleId, 1L);

            //when
            //then
            assertThatThrownBy(() -> battleNotification.changeImage(normalPlayer, newImageUrl))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 배틀_이미지URL을_제거한다() {
            //given
            final Battle battle = BattleFixture.initialBattleBuilder().id(1L).build();
            final BattleNotificationContent content = new BattleNotificationContent("12345678901234567890");
            final BattleNotificationImageUrl imageUrl = new BattleNotificationImageUrl("imageUrl");
            final BattleNotification battleNotification =
                    BattleNotification.withoutId(battle.getId(), content, imageUrl);

            //when
            final BattleParticipant manager = BattleParticipant.manager(battle.getId(), 1L);
            battleNotification.removeImage(manager);

            //then
            assertThat(battleNotification.getImageUrl()).isEmpty();
        }
    }
}
