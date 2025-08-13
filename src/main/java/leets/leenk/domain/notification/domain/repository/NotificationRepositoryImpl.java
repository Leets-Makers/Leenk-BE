package leets.leenk.domain.notification.domain.repository;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Query;
import lombok.RequiredArgsConstructor;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;

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
