package leets.leenk.domain.leenk.domain.service.scheduler;

import org.springframework.transaction.annotation.Transactional;
import leets.leenk.domain.leenk.application.usecase.LeenkSchedulerUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeenkAutoFinishScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final LeenkSchedulerUsecase leenkSchedulerUsecase;

    @Transactional
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void finishDue() {
        LocalDateTime now = LocalDateTime.now(KST);

        int affected = leenkSchedulerUsecase.finishDueLeenks(now);
        log.info("자동 종료된 링크 수 = {}", affected);

        int notifiedCount = leenkSchedulerUsecase.notifyFinishedLeenks(now);
        log.info("수동 종료된 링크 수 = {}", notifiedCount);

    }
}
