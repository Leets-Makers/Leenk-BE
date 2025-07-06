package leets.leenk.global.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsernamePasswordLoginRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
