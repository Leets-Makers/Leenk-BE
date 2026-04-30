package leets.leenk.domain.notification.domain.repository;

import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.notification.domain.entity.Notification;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Notification> findByUserIdAndNotificationTypeAndContentLeenkId(Long userId, NotificationType notificationType, Long leenkId) {
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("notificationType").is(notificationType)
                .and("content.leenkId").is(leenkId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Notification.class));
    }
}
