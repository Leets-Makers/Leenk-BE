package leets.leenk.domain.birthday.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.user.application.dto.response.UserProfileResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BirthdayUsersResponse(
    @field:Schema(description = "생일인 유저들")
    val users: List<UserProfileResponse>,
    @field:Schema(description = "받은 편지 개수", example = "100")
    val myBirthdayLettersCounts: Long? = null,
    @field:Schema(description = "새로온 편지가 왔는지 여부", example = "true")
    val hasNewLetters: Boolean? = null,
)
