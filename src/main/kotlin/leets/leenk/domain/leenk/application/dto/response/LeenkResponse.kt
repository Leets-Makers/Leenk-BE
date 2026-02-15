package leets.leenk.domain.leenk.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LeenkResponse(
    @field:Schema(description = "링크 Id", example = "1")
    val leenkId: Long,
    @field:Schema(description = "작성자 정보")
    val author: LeenkAuthorResponse,
    @field:Schema(description = "링크 제목")
    val title: String,
    @field:Schema(description = "현재 참여자 수", example = "2")
    val currentParticipants: Long,
    @field:Schema(description = "최대 참여자 수", example = "98")
    val maxParticipants: Long,
    @field:Schema(description = "링크 시작 시간", example = "2025-08-01T10:00:00")
    val startTime: LocalDateTime,
    @field:Schema(description = "링크 생성 시간", example = "2025-08-01T12:00:00")
    val createdAt: LocalDateTime,
    @field:Schema(description = "링크 수정 시간", example = "2025-08-01T12:00:00", nullable = true)
    val updatedAt: LocalDateTime?,
    @field:Schema(description = "링크 이미지", example = "https://s3.example.com/representative_image.jpg", nullable = true)
    val thumbNail: String?,
)
