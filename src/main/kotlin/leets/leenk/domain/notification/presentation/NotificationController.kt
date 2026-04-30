package leets.leenk.domain.notification.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse
import leets.leenk.domain.notification.application.exception.NotificationErrorCode
import leets.leenk.domain.notification.application.usecase.NotificationUseCase
import leets.leenk.global.auth.application.annotation.CurrentUserId
import leets.leenk.global.common.exception.ApiErrorCodeExample
import leets.leenk.global.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "NOTIFICATION", description = "알림 API")
@ApiErrorCodeExample(NotificationErrorCode::class)
@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationUseCase: NotificationUseCase,
) {
    @GetMapping
    @Operation(summary = "최근 알림 조회 API [무한스크롤] / 사용자의 최근 알림 목록을 페이지 단위로 조회합니다. pageNumber: 0부터 시작")
    fun getNotifications(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<NotificationListResponse> {
        val response = notificationUseCase.getNotifications(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.NOTIFICATION_READ_SUCCESS, response)
    }

    @GetMapping("/count")
    @Operation(summary = "알림 개수 조회 API")
    fun getNotificationCount(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
    ): CommonResponse<NotificationCountResponse> {
        val response = notificationUseCase.getNotificationCount(userId)

        return CommonResponse.success(ResponseCode.NOTIFICATION_COUNT_READ_SUCCESS, response)
    }

    @PatchMapping("/{notificationId}")
    @Operation(summary = "단일 알림 읽음 처리 API")
    fun markAsRead(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable notificationId: String,
    ): CommonResponse<Void?> {
        notificationUseCase.markAsRead(userId, notificationId)

        return CommonResponse.success(ResponseCode.NOTIFICATION_MARK_AS_READ_SUCCESS)
    }
}
