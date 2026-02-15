package leets.leenk.domain.leenk.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LeenkDetailResponse(
    @field:Schema(description = "링크 id", example = "1")
    val id: Long,
    @field:Schema(description = "작성자 정보")
    val author: LeenkAuthorResponse,
    @field:Schema(description = "카카오톡 id", example = "kakao123")
    val kakaoId: String,
    @field:Schema(description = "링크 상태", example = "RECRUITING")
    val status: LeenkStatus,
    @field:Schema(description = "제목", example = "전정도에서 번개 고고")
    val title: String,
    @field:Schema(description = "장소명", example = "전정도")
    val placeName: String,
    @field:Schema(description = "현재 참여 인원", example = "3")
    val currentParticipants: Long,
    @field:Schema(description = "최대 참여 인원", example = "20")
    val maxParticipants: Long,
    @field:Schema(description = "링크 일시", example = "2025-08-01T18:00:00")
    val startTime: LocalDateTime,
    @field:Schema(description = "상세 내용 (최대 200자)", example = "전정도에서 공부하실분~")
    val content: String,
    @field:Schema(description = "업로드된 이미지 URL(단건)", example = "https://s3.example.com/img1.jpg", nullable = true)
    val mediaUrl: String?,
    @field:Schema(description = "생성일", example = "2025-08-01T12:00:00")
    val createdAt: LocalDateTime,
    @field:Schema(description = "수정일", example = "2025-08-01T12:05:00", nullable = true)
    val updatedAt: LocalDateTime?,
    @field:Schema(description = "유저의 해당 링크 참여 여부", example = "true")
    val isParticipated: Boolean,
)
