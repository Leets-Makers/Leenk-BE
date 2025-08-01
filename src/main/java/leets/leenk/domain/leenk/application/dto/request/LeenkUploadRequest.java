package leets.leenk.domain.leenk.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record LeenkUploadRequest(

        @NotNull
        String title,

        @NotNull
        String place,

        @NotNull
        @Schema(description = "링크 일시")
        LocalDateTime meetingTime,

        @NotNull
        @Min(value = 3, message = "모집 인원은 최소 3명이어야 합니다")
        @Max(value = 99, message = "모집 인원은 최대 99명까지 가능 합니다")
        Long maxParticipants,

        @NotNull
        String content,

        @Size(max = 5, message = "이미지는 최대 5장까지 업로드할 수 있습니다")
        List<String> imageUrls
) {
}
