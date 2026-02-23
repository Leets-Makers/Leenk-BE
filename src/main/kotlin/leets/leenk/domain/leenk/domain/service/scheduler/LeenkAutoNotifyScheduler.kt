package leets.leenk.domain.leenk.domain.service.scheduler

import leets.leenk.domain.leenk.application.usecase.LeenkSchedulerUsecase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class LeenkAutoNotifyScheduler(
    private val leenkSchedulerUsecase: LeenkSchedulerUsecase,
) {
    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    fun scheduleLeenkNotifications() {
        val now = LocalDateTime.now(KST)

        leenkSchedulerUsecase.notifyLeenksStartingWithin30Minutes(now)

        leenkSchedulerUsecase.notifyHostsOfUnclosedLeenks(now)
    }

    companion object {
        private val KST = ZoneId.of("Asia/Seoul")
    }
}
