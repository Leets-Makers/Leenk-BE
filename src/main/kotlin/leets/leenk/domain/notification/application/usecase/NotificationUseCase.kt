package leets.leenk.domain.notification.application.usecase

import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse
import leets.leenk.domain.notification.application.mapper.NotificationResponseMapper
import leets.leenk.domain.notification.domain.service.NotificationGetService
import leets.leenk.domain.notification.domain.service.NotificationSaveService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationUseCase(
    private val notificationGetService: NotificationGetService,
    private val notificationSaveService: NotificationSaveService,
    private val notificationResponseMapper: NotificationResponseMapper,
) {
    @Transactional(readOnly = true)
    fun getNotifications(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): NotificationListResponse {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updateDate"))
        val notifications = notificationGetService.findPageByUserId(userId, pageable)

        return notificationResponseMapper.toNotificationListResponse(notifications)
    }

    @Transactional(readOnly = true)
    fun getNotificationCount(userId: Long): NotificationCountResponse {
        val count = notificationGetService.countUnreadByUserId(userId)

        return notificationResponseMapper.toCountResponse(count)
    }

    @Transactional
    fun markAsRead(
        userId: Long,
        notificationId: String,
    ) {
        val notification = notificationGetService.findByIdForUser(notificationId, userId)
        notification.markRead()
        notificationSaveService.save(notification)
    }
}
