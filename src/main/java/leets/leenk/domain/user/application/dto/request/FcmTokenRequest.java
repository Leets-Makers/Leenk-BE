package leets.leenk.domain.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank
        String fcmToken
) {
}
