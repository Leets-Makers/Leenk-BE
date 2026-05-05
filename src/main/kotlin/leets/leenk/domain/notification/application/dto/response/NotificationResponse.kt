package leets.leenk.domain.notification.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NotificationResponse(
    val deepLink: String?,
    val title: String,
    val body: String,
    val createDate: LocalDateTime,
    val updateDate: LocalDateTime,
    val details: List<NotificationDetailResponse>?,
    val leenkStartingSoonInfo: LeenkStartingSoonInfo?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LeenkStartingSoonInfo(
    val startTime: String?,
    val placeName: String?,
)
