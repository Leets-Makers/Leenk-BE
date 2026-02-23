package leets.leenk.domain.birthday.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpcomingBirthdayUsersResponse(
    @field:Schema(description = "생일일 유저들")
    val users: List<UpcomingBirthdayUserResponse>,
)
