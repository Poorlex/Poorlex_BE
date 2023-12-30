package com.poolex.poolex.battlenotification.service;

import com.poolex.poolex.battlenotification.domain.BattleNotification;
import com.poolex.poolex.battlenotification.domain.BattleNotificationContent;
import com.poolex.poolex.battlenotification.domain.BattleNotificationImageUrl;
import com.poolex.poolex.battlenotification.domain.BattleNotificationRepository;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationCreateRequest;
import com.poolex.poolex.battlenotification.service.dto.request.BattleNotificationUpdateRequest;
import com.poolex.poolex.participate.domain.BattleParticipant;
import com.poolex.poolex.participate.domain.BattleParticipantRepository;
import com.poolex.poolex.participate.domain.BattleParticipantRole;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BattleNotificationService {

    private final BattleNotificationRepository battleNotificationRepository;
    private final BattleParticipantRepository battleParticipantRepository;

    @Transactional
    public void createNotification(final Long battleId,
                                   final Long memberId,
                                   final BattleNotificationCreateRequest request) {
        validateIsManager(battleId, memberId);
        final BattleNotificationContent content = new BattleNotificationContent(request.getContent());

        if (Objects.isNull(request.getImageUrl())) {
            createNotificationWithoutImageUrl(battleId, content);
            return;
        }
        createNotificationWithImageUrl(battleId, content, request.getImageUrl());
    }

    private void validateIsManager(final Long battleId, final Long memberId) {
        final boolean isManager = battleParticipantRepository.existsByBattleIdAndMemberIdAndRole(
            battleId,
            memberId,
            BattleParticipantRole.MANAGER
        );

        if (!isManager) {
            throw new IllegalArgumentException("배틀의 매니저만이 공지를 등록할 수 있습니다.");
        }
    }

    private void createNotificationWithoutImageUrl(final Long battleId, final BattleNotificationContent content) {
        final BattleNotification battleNotification = BattleNotification.withoutIdAndImageUrl(battleId, content);
        battleNotificationRepository.save(battleNotification);
    }

    private void createNotificationWithImageUrl(final Long battleId,
                                                final BattleNotificationContent content,
                                                final String imageUrl) {
        final BattleNotificationImageUrl battleImageUrl = new BattleNotificationImageUrl(imageUrl);
        final BattleNotification battleNotification = BattleNotification.withoutId(battleId, content, battleImageUrl);
        battleNotificationRepository.save(battleNotification);
    }

    @Transactional
    public void updateNotification(final Long battleId,
                                   final Long memberId,
                                   final BattleNotificationUpdateRequest request) {
        validateIsManager(battleId, memberId);

        final BattleNotification battleNotification =
            battleNotificationRepository.findById(request.getBattleNotificationId())
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 배틀 공지가 존재하지 않습니다."));

        final BattleParticipant editor = BattleParticipant.manager(battleId, memberId);
        battleNotification.changeContent(editor, new BattleNotificationContent(request.getContent()));
        updateImageUrl(editor, battleNotification, request.getImageUrl());
    }

    private void updateImageUrl(final BattleParticipant editor,
                                final BattleNotification battleNotification,
                                final String imageUrl) {
        if (Objects.isNull(imageUrl)) {
            battleNotification.removeImage(editor);
            return;
        }
        battleNotification.changeImage(editor, new BattleNotificationImageUrl(imageUrl));
    }
}
