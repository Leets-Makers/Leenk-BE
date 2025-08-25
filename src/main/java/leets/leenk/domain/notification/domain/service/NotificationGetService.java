package leets.leenk.domain.notification.domain.service;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.notification.application.mapper.LeenkNotificationMapper;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.notification.application.mapper.FeedNotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NotificationGetService {

    private final NotificationRepository notificationRepository;
    private final FeedNotificationMapper feedNotificationMapper;
    private final LeenkNotificationMapper notificationMapper;

    public Slice<Notification> findRecentNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findPageByUserId(pageable, userId);
    }

    public Notification findOrCreateFirstReactionNotification(Reaction reaction) {
        return notificationRepository.findFeedFirstReactionByFeedId(NotificationType.FEED_FIRST_REACTION,
                        reaction.getFeed().getId())
                .orElseGet(() -> feedNotificationMapper.toFirstReactionNotification(reaction.getFeed()));
    }

    public Notification findOrCreateReactionCountNotification(Feed feed) {
        return notificationRepository.findByFeedId(NotificationType.FEED_REACTION_COUNT,
                        feed.getId())
                .orElseGet(() -> feedNotificationMapper.toReactionCountNotification(feed));
    }

    public Notification findOrCreateNewLeenkParticipantNotification(Leenk leenk, User existingUser) {
        Optional<Notification> existingNotification = notificationRepository
                .findByUserIdAndNotificationTypeAndContentLeenkId(
                        existingUser.getId(), NotificationType.NEW_LEENK_PARTICIPANT, leenk.getId());

        return existingNotification.orElseGet(() -> notificationMapper.toNewLeenkParticipantNotification(leenk,
                existingUser));
    }
}
