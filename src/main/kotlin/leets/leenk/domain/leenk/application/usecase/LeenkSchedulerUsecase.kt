package leets.leenk.domain.leenk.application.usecase

import leets.leenk.domain.leenk.domain.service.LeenkGetService
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LeenkSchedulerUsecase(
    private val leenkGetService: LeenkGetService,
    private val leenkNotificationUsecase: LeenkNotificationUsecase,
) {
    @Transactional
    fun finishDueLeenks(now: LocalDateTime): Int {
        val leenksToFinish = leenkGetService.findDueLeenks(now)

        leenksToFinish.forEach { leenk ->
            leenk.changeStatusToFinished()
            leenkNotificationUsecase.saveLeenkFinishedNotification(leenk)
        }
        return leenksToFinish.size
    }

    fun notifyLeenksStartingWithin30Minutes(now: LocalDateTime) {
        val leenksToNotify = leenkGetService.findLeenksStartingWithin30Minutes(now)

        leenksToNotify.forEach { leenkNotificationUsecase.saveLeenkStartingSoonNotification(it) }
    }

    @Transactional
    fun notifyFinishedLeenks(now: LocalDateTime): Int {
        val leenksToNotify = leenkGetService.findUnnotifiedFinishedLeenks(now)

        leenksToNotify.forEach { leenkNotificationUsecase.saveLeenkFinishedNotification(it) }
        return leenksToNotify.size
    }

    @Transactional
    fun notifyHostsOfUnclosedLeenks(now: LocalDateTime) {
        val leenksToNotify = leenkGetService.findOverdueRecruitingLeenksToNotify(now)

        leenksToNotify.forEach { leenkNotificationUsecase.saveLeenkStartedHostReminderNotification(it) }
    }
}
