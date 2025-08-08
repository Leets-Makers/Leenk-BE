package leets.leenk.domain.leenk.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record LeenkUploadRequest(

        @NotBlank
        @Schema(description = "제목", example = "전정도에서 번개 고고")
        @Size(max = 30, message = "제목은 최대 30자까지 입력할 수 있습니다")
        String title,

        @NotBlank
        @Schema(description = "상세 내용 (최대 200자)", example = "전정도에서 공부하실분~")
        @Size(max = 200, message = "상세 내용은 최대 200자까지 입력할 수 있습니다")
        String content,

        @NotBlank
        @Schema(description = "장소명", example = "전정도")
        @Size(max = 25, message = "장소는 최대 25자까지 입력할 수 있습니다")
        String placeName,

        @NotNull
        @Schema(description = "링크 일시", example = "2025-08-01T12:00:00")
        LocalDateTime startTime,

        @NotNull
        @Schema(description = "최대 참여 인원", example = "20")
        @Min(value = 3, message = "모집 인원은 최소 3명이어야 합니다")
        @Max(value = 99, message = "모집 인원은 최대 99명까지 가능 합니다")
        Long maxParticipants,

        @Schema(description = "업로드할 이미지", example = "https://s3.example.com/img1.jpg", nullable = true)
        String mediaUrl
) {
}
