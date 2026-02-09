package leets.leenk.domain.notification.domain.service

import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.repository.NotificationEntityRepository
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationSaveService(
    private val notificationRepository: NotificationRepository,
    private val notificationEntityRepository: NotificationEntityRepository,
) {
    fun save(notification: Notification): Notification = notificationRepository.save(notification)

    fun save(notification: NotificationEntity): NotificationEntity = notificationEntityRepository.save(notification)
}
