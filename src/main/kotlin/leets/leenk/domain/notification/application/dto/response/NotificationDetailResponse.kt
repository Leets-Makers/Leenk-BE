package leets.leenk.domain.notification.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NotificationDetailResponse(
    val userId: Long?,
    val name: String?,
    val milestone: Long?,
    val body: String,
    val createDate: String,
)
