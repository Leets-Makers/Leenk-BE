package leets.leenk.domain.notification.domain.repository

import leets.leenk.domain.notification.domain.entity.NotificationEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationEntityRepository : MongoRepository<NotificationEntity, String> {
}
