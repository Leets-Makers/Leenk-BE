package leets.leenk.domain.notification.domain.repository;

import java.util.Optional;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;

public interface NotificationRepositoryCustom {
    Optional<Notification> findFeedFirstReactionByFeedId(NotificationType type, Long feedId);

    Optional<Notification> findByFeedIdAndUserIdInFirstReactions(NotificationType type, Long feedId, Long userId);

    Optional<Notification> findByFeedId(NotificationType type, Long feedId);

    Optional<Notification> findByFeedIdAndReactionCount(NotificationType type, Long feedId, Long reactionCount);

    Optional<Notification> findByUserIdAndNotificationTypeAndContentLeenkId(Long userId, NotificationType notificationType, Long leenkId);
}
