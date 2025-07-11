package leets.leenk.domain.notification.domain.service;

import org.springframework.stereotype.Service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.notification.domain.entity.NotificationType;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationDuplicateCheckService {
    private final NotificationRepository notificationRepository;

    public boolean checkFirstReactionDuplicated(Reaction reaction) {
        return notificationRepository.findByFeedIdAndUserIdInFirstReactions(
                NotificationType.FEED_FIRST_REACTION, reaction.getFeed().getId(), reaction.getUser().getId()).isPresent();
    }

    public boolean checkReactionCountDuplicated(Long reactionCount, Feed feed) {
        return notificationRepository.findByFeedIdAndReactionCount(NotificationType.FEED_REACTION_COUNT, feed.getId(), reactionCount).isPresent();
    }
}
