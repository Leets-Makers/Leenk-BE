package leets.leenk.domain.leenk.domain.event

import java.time.LocalDateTime

sealed class LeenkDomainEvent {
    abstract val leenkId: Long
    abstract val leenkTitle: String

    data class NewLeenk(
        override val leenkId: Long,
        override val leenkTitle: String,
        val hostId: Long,
        val hostName: String,
    ) : LeenkDomainEvent()

    data class ParticipantJoined(
        override val leenkId: Long,
        override val leenkTitle: String,
        val newParticipantId: Long,
        val newParticipantName: String,
        val existingParticipantIds: List<Long>,
    ) : LeenkDomainEvent()

    data class ParticipantKicked(
        override val leenkId: Long,
        override val leenkTitle: String,
        val kickedUserId: Long,
    ) : LeenkDomainEvent()

    data class ParticipantLeft(
        override val leenkId: Long,
        override val leenkTitle: String,
        val leftUserId: Long,
        val leftUserName: String,
        val hostId: Long,
    ) : LeenkDomainEvent()

    data class Closed(
        override val leenkId: Long,
        override val leenkTitle: String,
        val participantIds: List<Long>,
    ) : LeenkDomainEvent()

    data class StartingSoon(
        override val leenkId: Long,
        override val leenkTitle: String,
        val participantIds: List<Long>,
        val placeId: Long?,
        val placeName: String?,
        val startTime: LocalDateTime?,
    ) : LeenkDomainEvent()

    data class Finished(
        override val leenkId: Long,
        override val leenkTitle: String,
        val participantIds: List<Long>,
    ) : LeenkDomainEvent()

    data class HostReminder(
        override val leenkId: Long,
        override val leenkTitle: String,
        val hostId: Long,
    ) : LeenkDomainEvent()
}
