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

    /**
     * DB에는 한 번에 여러 details를 저장하고, 푸시는 각 detail마다 개별 발송
     * Reaction Count 마일스톤처럼 여러 알림을 순차적으로 보내야 할 때 사용
     */
    fun sendOrUpdateWithMultiplePush(request: NotificationRequest)
}
