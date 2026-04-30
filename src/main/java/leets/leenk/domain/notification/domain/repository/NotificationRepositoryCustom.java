package leets.leenk.domain.notification.domain.repository;

import java.util.Optional;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;

public interface NotificationRepositoryCustom {
    Optional<Notification> findByUserIdAndNotificationTypeAndContentLeenkId(Long userId, NotificationType notificationType, Long leenkId);
}
