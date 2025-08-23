package leets.leenk.domain.leenk.domain.service.scheduler;

import jakarta.transaction.Transactional;
import leets.leenk.domain.leenk.application.usecase.LeenkSchedulerUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class LeenkAutoNotifyScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final LeenkSchedulerUsecase leenkSchedulerUsecase;

    @Transactional
    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    public void scheduleLeenkNotifications() {
        LocalDateTime now = LocalDateTime.now(KST);

        leenkSchedulerUsecase.notifyLeenksStartingWithin30Minutes(now);

        leenkSchedulerUsecase.notifyHostsOfUnclosedLeenks(now);

    }
}
