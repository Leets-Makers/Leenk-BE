package leets.leenk.domain.feed.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class ReactionRequest(
    @field:NotNull
    @field:Schema(description = "리액션 횟수")
    val reactionCount: Long,
)
