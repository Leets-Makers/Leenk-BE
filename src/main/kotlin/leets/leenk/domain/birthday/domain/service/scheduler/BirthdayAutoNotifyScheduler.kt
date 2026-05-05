package leets.leenk.domain.birthday.domain.service.scheduler

import leets.leenk.domain.birthday.application.usecase.BirthdaySchedulerUsecase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BirthdayAutoNotifyScheduler(
    private val birthdaySchedulerUsecase: BirthdaySchedulerUsecase,
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun scheduleBirthdayNotifications() {
        birthdaySchedulerUsecase.announceAndCelebrateBirthdays(LocalDate.now())
    }
}
