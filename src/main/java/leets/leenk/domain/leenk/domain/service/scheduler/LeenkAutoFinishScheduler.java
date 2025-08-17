package leets.leenk.domain.leenk.domain.service.scheduler;

import jakarta.transaction.Transactional;
import leets.leenk.domain.leenk.application.usecase.LeenkSchedulerUsecase;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.service.LeenkStatusBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeenkAutoFinishScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final LeenkSchedulerUsecase leenkSchedulerUsecase;
    private final LeenkStatusBatchService leenkStatusBatchService;

    @Transactional
    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    public void finishDue() {
        LocalDateTime now = LocalDateTime.now(KST);

        List<Leenk> finishedLeenks = leenkSchedulerUsecase.finishDueLeenks(now);
        if (!finishedLeenks.isEmpty()) {
            log.info("자동 종료된 링크 수 = {}", finishedLeenks.size());
        }

        leenkStatusBatchService.notifyStartSoon(now);
    }
}
