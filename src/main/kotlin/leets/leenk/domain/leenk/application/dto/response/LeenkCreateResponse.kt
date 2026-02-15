package leets.leenk.domain.leenk.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class LeenkCreateResponse(
    @field:Schema(description = "생성된 링크 id", example = "1")
    val id: Long,
)
