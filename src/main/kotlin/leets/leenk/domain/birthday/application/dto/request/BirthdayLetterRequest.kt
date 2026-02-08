package leets.leenk.domain.birthday.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class BirthdayLetterRequest(
    @field:NotBlank
    @field:Size(max = 40)
    val message: String,
)
