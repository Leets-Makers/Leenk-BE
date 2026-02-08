package leets.leenk.domain.notification.application.port

import leets.leenk.domain.notification.application.dto.NotificationRequest

interface NotificationPort {
    fun send(request: NotificationRequest)
    fun sendBatch(requests: List<NotificationRequest>)
}
