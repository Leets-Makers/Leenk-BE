package leets.leenk.domain.birthday.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BirthdayLetterRequest(
        @NotBlank
        @Size(max = 40)
        String message
) {
}
