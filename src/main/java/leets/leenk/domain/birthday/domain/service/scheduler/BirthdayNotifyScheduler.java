package leets.leenk.domain.birthday.domain.service.scheduler;

import leets.leenk.domain.notification.application.usecase.BirthdayNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BirthdayNotifyScheduler {

        private final BirthdayNotificationUsecase birthdayNotificationUsecase;

        @Scheduled(cron = "0 0 0 * * *")
        public void sendBirthdayNotifications() {
            LocalDate today = LocalDate.now();
            birthdayNotificationUsecase.announceUserBirthday(today);

            birthdayNotificationUsecase.celebrateBirthday(today);

        }
}
