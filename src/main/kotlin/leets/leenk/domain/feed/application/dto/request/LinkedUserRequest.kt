package leets.leenk.domain.feed.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class LinkedUserRequest(
    @field:NotNull
    @field:Positive
    @field:Schema(description = "태그할 사용자 ID")
    val userId: Long,
)
