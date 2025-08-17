package leets.leenk.domain.leenk.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.service.LeenkGetService;
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
    private final LeenkGetService leenkGetService;

    private final LeenkNotificationUsecase leenkNotificationUsecase;

    @Transactional
    public int finishDueLeenks(LocalDateTime now) {
        List<Leenk> leenksToFinish = leenkStatusBatchService.findDueLeenks(now);

        leenksToFinish.forEach(leenk -> {
            leenk.changeStatusToFinished();
            leenkNotificationUsecase.saveLeenkFinishedNotification(leenk);
        });
        return leenksToFinish.size();
    }

    public void notifyLeenksStartingWithin30Minutes(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkGetService.findLeenksStartingWithin30Minutes(now);

        leenksToNotify.forEach(leenkNotificationUsecase::saveLeenkStartingSoonNotification);
    }
}
