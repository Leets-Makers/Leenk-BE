package leets.leenk.domain.leenk.application.mapper

import leets.leenk.domain.leenk.application.dto.response.LeenkAuthorResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LeenkParticipantsMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toParticipants(
        leenk: Leenk,
        user: User,
        joinedAt: LocalDateTime,
    ): LeenkParticipants =
        LeenkParticipants(
            leenk = leenk,
            participant = user,
            joinedAt = joinedAt,
        )

    fun toLeenkAuthorResponse(user: User): LeenkAuthorResponse = LeenkAuthorResponse(userProfileMapper.toProfile(user))

    fun toLeenkParticipantsListResponse(
        leenk: Leenk,
        participants: List<LeenkParticipants>,
    ): LeenkParticipantsListResponse {
        val responses =
            participants.map { leenkParticipants ->
                val participantUser = leenkParticipants.participant

                LeenkParticipantResponse(
                    participant = toLeenkAuthorResponse(participantUser),
                    kakaoTalkId = participantUser.kakaoTalkId,
                    currentParticipants = leenk.currentParticipants,
                    maxParticipants = leenk.maxParticipants,
                    joinedAt = leenkParticipants.joinedAt!!,
                    isHost = participantUser.id == leenk.author.id,
                )
            }

        return LeenkParticipantsListResponse(responses)
    }
}
