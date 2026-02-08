package leets.leenk.domain.birthday.domain.service.scheduler

import leets.leenk.domain.notification.application.usecase.BirthdayNotificationUsecase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BirthdayNotifyScheduler(
    private val birthdayNotificationUsecase: BirthdayNotificationUsecase,
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun sendBirthdayNotifications() {
        val today = LocalDate.now()
        birthdayNotificationUsecase.announceUserBirthday(today)

        birthdayNotificationUsecase.celebrateBirthday(today)
    }
}
