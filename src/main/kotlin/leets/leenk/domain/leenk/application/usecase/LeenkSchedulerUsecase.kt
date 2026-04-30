package leets.leenk.domain.leenk.application.usecase

import leets.leenk.domain.leenk.domain.event.LeenkDomainEvent
import leets.leenk.domain.leenk.domain.service.LeenkGetService
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsGetService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LeenkSchedulerUsecase(
    private val leenkGetService: LeenkGetService,
    private val leenkParticipantsGetService: LeenkParticipantsGetService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun finishDueLeenks(now: LocalDateTime): Int {
        val leenksToFinish = leenkGetService.findDueLeenks(now)

        leenksToFinish.forEach { leenk ->
            leenk.changeStatusToFinished()

            val participantIds = leenkParticipantsGetService.findAllByLeenk(leenk).map { it.participant.id }
            eventPublisher.publishEvent(
                LeenkDomainEvent.Finished(
                    leenkId = leenk.id!!,
                    leenkTitle = leenk.title,
                    participantIds = participantIds,
                )
            )
        }
        return leenksToFinish.size
    }

    fun notifyLeenksStartingWithin30Minutes(now: LocalDateTime) {
        val leenksToNotify = leenkGetService.findLeenksStartingWithin30Minutes(now)

        leenksToNotify.forEach { leenk ->
            val participantIds = leenkParticipantsGetService.findAllByLeenk(leenk).map { it.participant.id }
            eventPublisher.publishEvent(
                LeenkDomainEvent.StartingSoon(
                    leenkId = leenk.id!!,
                    leenkTitle = leenk.title,
                    participantIds = participantIds,
                    placeId = leenk.location.id,
                    placeName = leenk.location.placeName,
                    startTime = leenk.startTime,
                )
            )
        }
    }

    @Transactional
    fun notifyFinishedLeenks(now: LocalDateTime): Int {
        val leenksToNotify = leenkGetService.findUnnotifiedFinishedLeenks(now)

        leenksToNotify.forEach { leenk ->
            val participantIds = leenkParticipantsGetService.findAllByLeenk(leenk).map { it.participant.id }
            eventPublisher.publishEvent(
                LeenkDomainEvent.Finished(
                    leenkId = leenk.id!!,
                    leenkTitle = leenk.title,
                    participantIds = participantIds,
                )
            )
        }
        return leenksToNotify.size
    }

    @Transactional
    fun notifyHostsOfUnclosedLeenks(now: LocalDateTime) {
        val leenksToNotify = leenkGetService.findOverdueRecruitingLeenksToNotify(now)

        leenksToNotify.forEach { leenk ->
            eventPublisher.publishEvent(
                LeenkDomainEvent.HostReminder(
                    leenkId = leenk.id!!,
                    leenkTitle = leenk.title,
                    hostId = leenk.author.id,
                )
            )
        }
    }
}
