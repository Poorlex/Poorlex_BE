package com.poorlex.refactoring.battle.notification.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long battleId;
    @Embedded
    private BattleNotificationContent content;
    @Embedded
    private BattleNotificationImageUrl imageUrl;

    private BattleNotification(final Long id,
                               final Long battleId,
                               final BattleNotificationContent content,
                               final BattleNotificationImageUrl imageUrl) {
        this.id = id;
        this.battleId = battleId;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public static BattleNotification withoutId(final Long battleId,
                                               final BattleNotificationContent content,
                                               final BattleNotificationImageUrl imageUrl) {
        return new BattleNotification(null, battleId, content, imageUrl);
    }

    public static BattleNotification withoutIdAndImageUrl(final Long battleId,
                                                          final BattleNotificationContent content) {
        return new BattleNotification(null, battleId, content, null);
    }

    public void changeContent(final BattleNotificationContent newContent) {
        this.content = newContent;
    }

    public void removeImage() {
        this.imageUrl = null;
    }

    public void changeImage(final BattleNotificationImageUrl newImageUrl) {
        this.imageUrl = newImageUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getBattleId() {
        return battleId;
    }

    public String getContent() {
        return content.getValue();
    }

    public Optional<String> getImageUrl() {
        if (Objects.isNull(imageUrl)) {
            return Optional.empty();
        }
        return Optional.of(imageUrl.getValue());
    }
}
