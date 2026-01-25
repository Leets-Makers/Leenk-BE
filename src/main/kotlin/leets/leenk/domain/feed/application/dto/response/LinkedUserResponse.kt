package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.user.application.dto.response.UserProfileResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LinkedUserResponse(
    @field:JsonUnwrapped
    @field:Schema(implementation = UserProfileResponse::class)
    val user: UserProfileResponse,

    @field:Schema(description = "작성자 여부", example = "true")
    val isAuthor: Boolean,
)
