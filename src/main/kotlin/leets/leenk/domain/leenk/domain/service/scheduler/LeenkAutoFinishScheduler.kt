package leets.leenk.domain.leenk.domain.service.scheduler

import leets.leenk.domain.leenk.application.usecase.LeenkSchedulerUsecase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class LeenkAutoFinishScheduler(
    private val leenkSchedulerUsecase: LeenkSchedulerUsecase,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    fun finishDue() {
        val now = LocalDateTime.now(KST)

        try {
            val affected = leenkSchedulerUsecase.finishDueLeenks(now)
            log.info("자동 종료 후 링크 발송 수 = {}", affected)
        } catch (e: Exception) {
            log.error("자동 종료 처리 실패", e)
        }

        try {
            val notifiedCount = leenkSchedulerUsecase.notifyFinishedLeenks(now)
            log.info("수동 종료 알림 발송 수 = {}", notifiedCount)
        } catch (e: Exception) {
            log.error("수동 종료 알림 발송 실패", e)
        }
    }

    companion object {
        private val KST = ZoneId.of("Asia/Seoul")
    }
}
