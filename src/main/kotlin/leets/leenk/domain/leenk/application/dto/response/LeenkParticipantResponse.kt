package leets.leenk.domain.leenk.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LeenkParticipantResponse(
    @field:Schema(description = "참여자 정보")
    val participant: LeenkAuthorResponse,
    @field:Schema(description = "참여자 카카오톡 ID")
    val kakaoTalkId: String,
    @field:Schema(description = "현재 참여 인원", example = "3")
    val currentParticipants: Long,
    @field:Schema(description = "최대 참여 인원", example = "20")
    val maxParticipants: Long,
    @field:Schema(description = "참여 시각")
    val joinedAt: LocalDateTime,
    @field:Schema(description = "방장 여부")
    val isHost: Boolean,
)
