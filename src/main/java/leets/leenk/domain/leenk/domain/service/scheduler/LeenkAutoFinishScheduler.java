package leets.leenk.domain.leenk.domain.service.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import leets.leenk.domain.leenk.domain.service.LeenkStatusBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeenkAutoFinishScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final LeenkStatusBatchService leenkStatusBatchService;

    @Transactional
    @Scheduled(cron = "0 0/30 * * * *", zone = "Asia/Seoul")
    public void finishDue() {
        LocalDateTime now = LocalDateTime.now(KST);
        int affected = leenkStatusBatchService.finishDueLeenks(now);
        log.info("자동 종료된 링크 수 = {}", affected);
    }
}
