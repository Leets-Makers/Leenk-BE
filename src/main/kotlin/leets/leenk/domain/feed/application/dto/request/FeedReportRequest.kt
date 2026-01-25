package leets.leenk.domain.feed.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FeedReportRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val report: String,
)
