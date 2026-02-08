package leets.leenk.domain.notification.application.policy

import leets.leenk.domain.notification.domain.enums.NotificationType
import org.springframework.stereotype.Component

@Component
class NotificationPolicy(
) {
    fun shouldNotify(userId: Long, type: NotificationType): Boolean {
        return true
    }

    fun canPublishPush(userId: Long): Boolean {
        return true
    }
}
