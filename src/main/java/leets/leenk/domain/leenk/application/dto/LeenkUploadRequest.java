package leets.leenk.domain.leenk.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record LeenkUploadRequest(

        @NotNull
        String title,

        @NotNull
        String place,

        @NotNull @Schema(description = "링크 일시")
        LocalDateTime meetingTime,

        @NotNull @Min(value = 3, message = "모집 인원은 최소 3명이어야 합니다")
        Long maxParticipants,

        @NotNull
        String content,

        @NotNull
        List<String> imageUrls
) {
}
