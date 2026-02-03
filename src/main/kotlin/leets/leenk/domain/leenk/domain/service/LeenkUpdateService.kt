package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest
import leets.leenk.domain.leenk.application.exception.MaxParticipantsTooLowException
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.Location
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LeenkUpdateService {
    fun updateLeenk(leenk: Leenk, location: Location, request: LeenkUpdateRequest) {
        updateTitle(leenk, request.title)
        updateContent(leenk, request.content)
        updateStartTime(leenk, request.startTime)
        updateMaxParticipants(leenk, request.maxParticipants)
        updatePlaceName(location, request.placeName)
    }

    private fun updateTitle(leenk: Leenk, title: String?) {
        if (!title.isNullOrBlank()) {
            leenk.updateTitle(title)
        }
    }

    private fun updateContent(leenk: Leenk, content: String?) {
        if (!content.isNullOrBlank()) {
            leenk.updateContent(content)
        }
    }

    private fun updateStartTime(leenk: Leenk, startTime: LocalDateTime?) {
        if (startTime != null) {
            leenk.updateStartTime(startTime)
        }
    }

    private fun updateMaxParticipants(leenk: Leenk, maxParticipants: Long?) {
        if (maxParticipants == null) {
            return
        }

        val currentCount = leenk.currentParticipants

        if (maxParticipants < currentCount) {
            throw MaxParticipantsTooLowException()
        }

        leenk.updateMaxParticipants(maxParticipants)
    }

    private fun updatePlaceName(location: Location?, placeName: String?) {
        if (location != null && !placeName.isNullOrBlank()) {
            location.updatePlaceName(placeName)
        }
    }
}
