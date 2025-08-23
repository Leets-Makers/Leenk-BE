package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkDetailResponse(

        @Schema(description = "링크 id", example = "1")
        Long id,

        @Schema(description = "작성자 정보")
        LeenkAuthorResponse author,

        @Schema(description = "카카오톡 id", example = "kakao123")
        String kakaoId,

        @Schema(description = "링크 상태", example = "RECRUITING")
        LeenkStatus status,

        @Schema(description = "제목", example = "전정도에서 번개 고고")
        String title,

        @Schema(description = "장소명", example = "전정도")
        String placeName,

        @Schema(description = "현재 참여 인원", example = "3")
        Long currentParticipants,

        @Schema(description = "최대 참여 인원", example = "20")
        Long maxParticipants,

        @Schema(description = "링크 일시", example = "2025-08-01T18:00:00")
        LocalDateTime startTime,

        @Schema(description = "상세 내용 (최대 200자)", example = "전정도에서 공부하실분~")
        String content,

        @Schema(description = "업로드된 이미지 URL(단건)", example = "https://s3.example.com/img1.jpg", nullable = true)
        String mediaUrl,

        @Schema(description = "생성일", example = "2025-08-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일", example = "2025-08-01T12:05:00", nullable = true)
        LocalDateTime updatedAt,

        @Schema(description = "유저의 해당 링크 참여 여부", example = "true")
        boolean isParticipated
) {
}
