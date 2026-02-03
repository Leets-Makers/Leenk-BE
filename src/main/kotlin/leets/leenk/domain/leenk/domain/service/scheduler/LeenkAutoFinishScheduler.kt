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

        try {
            int affected = leenkSchedulerUsecase.finishDueLeenks(now);
            log.info("자동 종료 후 링크 발송 수 = {}", affected);
        } catch (Exception e) {
            log.error("자동 종료 처리 실패", e);
        }

        try {
            int notifiedCount = leenkSchedulerUsecase.notifyFinishedLeenks(now);
            log.info("수동 종료 알림 발송 수 = {}", notifiedCount);
        } catch (Exception e) {
            log.error("수동 종료 알림 발송 실패", e);
        }

    }
}
