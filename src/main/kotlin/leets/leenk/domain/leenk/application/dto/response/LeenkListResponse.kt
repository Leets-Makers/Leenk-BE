package leets.leenk.domain.leenk.application.dto.response

import leets.leenk.global.common.dto.CommonPageableResponse

data class LeenkListResponse(
    val leenks: List<LeenkResponse>,
    val pageable: CommonPageableResponse,
)
