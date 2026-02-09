package leets.leenk.domain.notification.application.policy

import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import org.springframework.stereotype.Component

// TODO: 마이그레이션 이후NotificationSetting을 사용
@Component
class NotificationPolicy {
    fun shouldNotify(
        userId: Long,
        type: NotificationType,
    ): Boolean = true

    fun canPublishPush(userId: Long): Boolean = true
}
