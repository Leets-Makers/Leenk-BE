package leets.leenk.domain.leenk.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LeenkReportRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val report: String,
)
