package leets.leenk.domain.user.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BirthdayRequest(
        @NotNull
        LocalDate birthday
) {
}
