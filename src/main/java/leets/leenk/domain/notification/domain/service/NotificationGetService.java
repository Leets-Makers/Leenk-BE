package leets.leenk.domain.notification.domain.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.notification.application.mapper.NotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.NotificationType;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationGetService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public Slice<Notification> findRecentNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findPageByUserId(pageable, userId);
    }

    public Notification findOrCreateFirstReactionNotification(Reaction reaction) {
        return notificationRepository.findFeedFirstReactionByFeedId(NotificationType.FEED_FIRST_REACTION,
                        reaction.getFeed().getId())
                .orElseGet(() -> notificationMapper.toFirstReactionNotification(reaction.getFeed()));
    }

    public Notification findOrCreateReactionCountNotification(Feed feed) {
        return notificationRepository.findByFeedId(NotificationType.FEED_REACTION_COUNT,
                        feed.getId())
                .orElseGet(() -> notificationMapper.toReactionCountNotification(feed));
    }
}
