package leets.leenk.domain.leenk.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.service.LeenkStatusBatchService;
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LeenkSchedulerUsecase {

    private final LeenkStatusBatchService leenkStatusBatchService;

    private final LeenkNotificationUsecase leenkNotificationUsecase;

    @Transactional
    public List<Leenk> finishDueLeenks(LocalDateTime now) {
        List<Leenk> leenksToFinish = leenkStatusBatchService.findDueLeenks(now);

        leenksToFinish.forEach(Leenk::changeStatusToFinished);
        return leenksToFinish;
    }

    public void notifyLeenksStartingWithin30Minutes(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkStatusBatchService.findLeenksStartingSoon(now);

        leenksToNotify.forEach(leenkNotificationUsecase::saveLeenkStartingSoonNotification);
    }
}
