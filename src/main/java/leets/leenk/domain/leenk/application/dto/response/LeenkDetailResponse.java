package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkDetailResponse(

        @Schema(description = "링크 id", example = "1")
        Long id,

        @Schema(description = "작성자 id", example = "1")
        Long userId,

        @Schema(description = "작성자 이름", example = "이지훈")
        String userName,

        @Schema(description = "작성자 프로필 이미지 URL", example = "https://s3.example.com/user/profile.jpg", nullable = true)
        String userProfileImage,

        @Schema(description = "제목", example = "전정도에서 번개 고고")
        String title,

        @Schema(description = "장소명", example = "전정도")
        String placeName,

        @Schema(description = "현재 참여 인원", example = "3")
        Long currentParticipants,

        @Schema(description = "최대 참여 인원", example = "20")
        Long maxParticipants,

        @Schema(description = "번개 시작 시간", example = "2025-08-01T18:00:00")
        LocalDateTime startTime,

        @Schema(description = "상세 내용 (최대 200자)", example = "전정도에서 공부하실분~", nullable = true)
        String content,

        @Schema(description = "업로드된 이미지 URL 리스트(0~5장, 순서 보장)", example = "[\"https://s3.example.com/img1.jpg\", \"https://s3.example.com/img2.jpg\"]", nullable = true)
        List<String> images,

        @Schema(description = "생성일", example = "2025-08-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일", example = "2025-08-01T12:05:00", nullable = true)
        LocalDateTime updatedAt

) {
}
