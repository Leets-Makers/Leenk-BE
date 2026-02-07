package leets.leenk.domain.feed.application.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentWriteRequest(
    @field:NotBlank
    val comment: String,
)
