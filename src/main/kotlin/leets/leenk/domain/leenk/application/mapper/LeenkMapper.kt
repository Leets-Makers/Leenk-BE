package leets.leenk.domain.leenk.application.mapper

import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest
import leets.leenk.domain.leenk.application.dto.response.LeenkAuthorResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkResponse
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.Location
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.dto.PageableMapperUtil
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class LeenkMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toLeenk(
        author: User,
        location: Location,
        request: LeenkUploadRequest,
    ): Leenk =
        Leenk(
            author = author,
            location = location,
            title = request.title,
            content = request.content,
            startTime = request.startTime,
            maxParticipants = request.maxParticipants,
        )

    fun toLeenkAuthorResponse(leenk: Leenk): LeenkAuthorResponse =
        LeenkAuthorResponse(userProfileMapper.toProfile(leenk.author))

    fun toLeenkListResponse(
        slice: Slice<Leenk>,
        mediaMap: Map<Long, List<Media>>,
    ): LeenkListResponse {
        val responses = toLeenkResponses(slice.content, mediaMap)
        return LeenkListResponse(responses, PageableMapperUtil.from(slice))
    }

    fun toLeenkDetailResponse(
        leenk: Leenk,
        mediaUrl: String?,
        isParticipated: Boolean,
    ): LeenkDetailResponse {
        val content = leenk.content ?: ""

        return LeenkDetailResponse(
            id = leenk.id!!,
            author = toLeenkAuthorResponse(leenk),
            kakaoId = leenk.author.getKakaoTalkId(),
            status = leenk.status,
            title = leenk.title,
            placeName = leenk.location.placeName,
            currentParticipants = leenk.currentParticipants,
            maxParticipants = leenk.maxParticipants,
            startTime = leenk.startTime,
            content = content,
            mediaUrl = mediaUrl,
            createdAt = leenk.getCreateDate(),
            updatedAt = leenk.getUpdateDate(),
            isParticipated = isParticipated,
        )
    }

    private fun toLeenkResponses(
        leenks: List<Leenk>,
        mediaMap: Map<Long, List<Media>>,
    ): List<LeenkResponse> =
        leenks.map { leenk ->
            val medias = mediaMap.getOrDefault(leenk.id, emptyList())
            val representative = medias.firstOrNull()
            toLeenkResponse(leenk, representative)
        }

    private fun toLeenkResponse(
        leenk: Leenk,
        representative: Media?,
    ): LeenkResponse {
        val imageUrl = representative?.getThumbnailUrl()

        return LeenkResponse(
            leenkId = leenk.id!!,
            author = toLeenkAuthorResponse(leenk),
            title = leenk.title,
            currentParticipants = leenk.currentParticipants,
            maxParticipants = leenk.maxParticipants,
            startTime = leenk.startTime,
            createdAt = leenk.getCreateDate(),
            updatedAt = leenk.getUpdateDate(),
            thumbNail = imageUrl,
        )
    }
}
