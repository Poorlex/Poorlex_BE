package com.poorlex.poorlex.battlenotification.domain;

import com.poorlex.poorlex.participate.domain.BattleParticipant;
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

    public void changeContent(final BattleParticipant editor, final BattleNotificationContent newContent) {
        validateEditorIsManagerInSameBattle(editor);
        this.content = newContent;
    }

    private void validateEditorIsManagerInSameBattle(final BattleParticipant editor) {
        if (editor.isNormalPlayer() || !editor.hasSameBattleId(battleId)) {
            throw new IllegalArgumentException();
        }
    }

    public void removeImage(final BattleParticipant editor) {
        validateEditorIsManagerInSameBattle(editor);
        this.imageUrl = null;
    }

    public void changeImage(final BattleParticipant editor, final BattleNotificationImageUrl newImageUrl) {
        if (Objects.isNull(newImageUrl)) {
            removeImage(editor);
        }
        validateEditorIsManagerInSameBattle(editor);
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
