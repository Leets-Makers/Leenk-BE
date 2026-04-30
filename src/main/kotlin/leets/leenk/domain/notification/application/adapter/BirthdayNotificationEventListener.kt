package leets.leenk.domain.notification.application.adapter

import leets.leenk.domain.birthday.domain.event.BirthdayDomainEvent
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BirthdayNotificationEventListener(
    private val notificationPort: NotificationPort,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onTodayBirthday(event: BirthdayDomainEvent.TodayBirthday) {
        // 생일자에게 축하 알림
        val celebrateRequests =
            event.birthdayUsers.map { birthdayUser ->
                NotificationRequest(
                    userId = birthdayUser.id,
                    type = NotificationType.BIRTHDAY_CELEBRATE,
                    targetId = birthdayUser.id,
                    name = birthdayUser.name,
                )
            }
        notificationPort.sendBatch(celebrateRequests)

        // 다른 사용자들에게 생일자별 공지
        event.birthdayUsers.forEach { birthdayUser ->
            val announceRequests =
                event.receiverIds
                    .filter { it != birthdayUser.id }
                    .map { receiverId ->
                        NotificationRequest(
                            userId = receiverId,
                            type = NotificationType.BIRTHDAY_ANNOUNCEMENT,
                            targetId = birthdayUser.id,
                            name = birthdayUser.name,
                        )
                    }
            if (announceRequests.isNotEmpty()) {
                notificationPort.sendBatch(announceRequests)
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onLetterSent(event: BirthdayDomainEvent.LetterSent) {
        notificationPort.send(
            NotificationRequest(
                userId = event.receiverId,
                type = NotificationType.BIRTHDAY_LETTER,
                targetId = event.letterId,
                name = event.senderName,
            ),
        )
    }
}
