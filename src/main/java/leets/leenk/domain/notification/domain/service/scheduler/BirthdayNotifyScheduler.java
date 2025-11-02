package leets.leenk.domain.notification.domain.service.scheduler;

import leets.leenk.domain.notification.application.usecase.BirthdayNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BirthdayNotifyScheduler {

        private static final ZoneId KST = ZoneId.of("Asia/Seoul");

        private final BirthdayNotificationUsecase birthdayNotificationUsecase;

        @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
        public void sendBirthdayNotifications() {
            LocalDate today = LocalDate.now(KST);
            birthdayNotificationUsecase.announceUserBirthday(today);

        }
}
