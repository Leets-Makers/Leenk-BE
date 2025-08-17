package leets.leenk.domain.notification.domain.repository;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Notification> findFeedFirstReactionByFeedId(NotificationType type, Long feedId) {
        Query query = new Query(Criteria.where("notificationType").is(type)
                .and("content.feedId").is(feedId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Notification.class));
    }

    @Override
    public Optional<Notification> findByFeedIdAndUserIdInFirstReactions(NotificationType type, Long feedId,
                                                                        Long userId) {
        Query query = new Query(Criteria.where("notificationType").is(type)
                .and("content.feedId").is(feedId)
                .and("content.feedFirstReactions.userId").is(userId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Notification.class));
    }

    @Override
    public Optional<Notification> findByFeedId(NotificationType type, Long feedId) {
        Query query = new Query(Criteria.where("notificationType").is(type)
                .and("content.feedId").is(feedId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Notification.class));
    }

    @Override
    public Optional<Notification> findByFeedIdAndReactionCount(NotificationType type, Long feedId, Long reactionCount) {
        Query query = new Query(Criteria.where("notificationType").is(type)
                .and("content.feedId").is(feedId)
                .and("content.feedReactionCounts.reactionCount").is(reactionCount));
        return Optional.ofNullable(mongoTemplate.findOne(query, Notification.class));
    }
}
