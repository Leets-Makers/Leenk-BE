package leets.leenk.domain.notification.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.global.common.dto.CommonPageableResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NotificationListResponse(
    @field:Schema(description = "알림 목록")
    val notificationResponses: List<NotificationResponse>,
    @field:Schema(description = "페이징 정보")
    val pageable: CommonPageableResponse,
)
