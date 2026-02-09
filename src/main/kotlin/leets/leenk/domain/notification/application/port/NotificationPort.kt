package leets.leenk.domain.notification.application.port

import leets.leenk.domain.notification.application.dto.NotificationRequest

interface NotificationPort {
    /**
     * 단일 알림 발행
     */
    fun send(request: NotificationRequest)

    /**
     * 다수 알림 일괄 발행
     */
    fun sendBatch(requests: List<NotificationRequest>)

    /**
     * 기존 알림 업데이트 (제목, 본문 변경)
     * 기존 알림이 있으면 업데이트, 없으면 새로 생성
     */
    fun sendOrUpdate(request: NotificationRequest)
}
