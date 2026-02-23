package leets.leenk.domain.birthday.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpcomingBirthdayUserResponse(
    @field:JsonUnwrapped
    @field:Schema(implementation = UserProfileResponse::class)
    val profile: UserProfileResponse,
    @field:Schema(description = "사용자 생일", example = "2001-06-15")
    val birthday: LocalDate,
)
