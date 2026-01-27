package leets.leenk.domain.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank
        String kakaoTalkId,
        @Size(max = 200)
        String introduction,
        String profileImage,
        LocalDate birthday,
        @Size(max = 4)
        String mbti
) {
}
