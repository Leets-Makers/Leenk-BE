package leets.leenk.domain.birthday.application.dto.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import java.time.LocalDateTime

data class MyBirthdayLettersResponse(
    @field:Schema(description = "편지 id", example = "1")
    val letterId: Long,
    @field:JsonUnwrapped
    @field:Schema(implementation = UserProfileResponse::class)
    val author: UserProfileResponse,
    @field:Schema(description = "편지 내용", example = "생일 축하한데이")
    val message: String,
    @field:Schema(description = "편지 작성 일자", example = "2025-11-30T00:00:00")
    val createdAt: LocalDateTime,
)
