package com.poorlex.refactoring.battle.notification.service;

import com.poorlex.refactoring.battle.notification.domain.BattleNotification;
import com.poorlex.refactoring.battle.notification.domain.BattleNotificationContent;
import com.poorlex.refactoring.battle.notification.domain.BattleNotificationImageUrl;
import com.poorlex.refactoring.battle.notification.domain.BattleNotificationRepository;
import com.poorlex.refactoring.battle.notification.service.dto.request.BattleNotificationCreateRequest;
import com.poorlex.refactoring.battle.notification.service.dto.request.BattleNotificationUpdateRequest;
import com.poorlex.refactoring.battle.notification.service.dto.response.BattleNotificationResponse;
import com.poorlex.refactoring.battle.notification.service.event.BattleNotificationChangedEvent;
import com.poorlex.refactoring.battle.notification.service.validate.BattleNotificationValidator;
import com.poorlex.refactoring.config.event.Events;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BattleNotificationCommandService {

    private final BattleNotificationValidator validator;
    private final BattleNotificationRepository battleNotificationRepository;

    public void createNotification(final Long battleId,
                                   final Long memberId,
                                   final BattleNotificationCreateRequest request) {
        validator.hasMangerRoleByBattleIdAndMemberId(battleId, memberId);
        final BattleNotificationContent content = new BattleNotificationContent(request.getContent());

        if (Objects.isNull(request.getImageUrl())) {
            createNotificationWithoutImageUrl(battleId, content);
        } else {
            createNotificationWithImageUrl(battleId, content, request.getImageUrl());
        }

        Events.raise(new BattleNotificationChangedEvent(battleId, memberId));
    }

    private void createNotificationWithoutImageUrl(final Long battleId, final BattleNotificationContent content) {
        battleNotificationRepository.save(BattleNotification.withoutIdAndImageUrl(battleId, content));
    }

    private void createNotificationWithImageUrl(final Long battleId,
                                                final BattleNotificationContent content,
                                                final String imageUrl) {
        final BattleNotificationImageUrl battleImageUrl = new BattleNotificationImageUrl(imageUrl);
        battleNotificationRepository.save(BattleNotification.withoutId(battleId, content, battleImageUrl));
    }

    public void updateNotification(final Long battleId,
                                   final Long memberId,
                                   final BattleNotificationUpdateRequest request) {
        validator.hasMangerRoleByBattleIdAndMemberId(battleId, memberId);
        final BattleNotification battleNotification = battleNotificationRepository.findByBattleId(battleId)
            .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 배틀 공지가 존재하지 않습니다."));

        battleNotification.changeContent(new BattleNotificationContent(request.getContent()));
        updateImageUrl(battleNotification, request.getImageUrl());
        Events.raise(new BattleNotificationChangedEvent(battleId, memberId));
    }

    private void updateImageUrl(final BattleNotification battleNotification, final String imageUrl) {
        if (Objects.isNull(imageUrl)) {
            battleNotification.removeImage();
            return;
        }
        battleNotification.changeImage(new BattleNotificationImageUrl(imageUrl));
    }

    public BattleNotificationResponse findNotificationByBattleId(final Long battleId) {
        final Optional<BattleNotification> battleNotification = battleNotificationRepository.findByBattleId(battleId);

        return battleNotification.map(BattleNotificationResponse::from)
            .orElseGet(BattleNotificationResponse::empty);
    }
}
