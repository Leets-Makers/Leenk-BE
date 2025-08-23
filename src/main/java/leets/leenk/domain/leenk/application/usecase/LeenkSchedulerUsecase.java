package leets.leenk.domain.leenk.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.service.LeenkGetService;
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LeenkSchedulerUsecase {

    private final LeenkGetService leenkGetService;

    private final LeenkNotificationUsecase leenkNotificationUsecase;

    @Transactional
    public int finishDueLeenks(LocalDateTime now) {
        List<Leenk> leenksToFinish = leenkGetService.findDueLeenks(now);

        leenksToFinish.forEach(leenk -> {
            leenk.changeStatusToFinished();
            leenkNotificationUsecase.saveLeenkFinishedNotification(leenk);
            leenk.markAsFinishedNotified();
        });
        return leenksToFinish.size();
    }

    public void notifyLeenksStartingWithin30Minutes(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkGetService.findLeenksStartingWithin30Minutes(now);

        leenksToNotify.forEach(leenkNotificationUsecase::saveLeenkStartingSoonNotification);
    }

    @Transactional
    public int notifyFinishedLeenks(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkGetService.findUnnotifiedFinishedLeenks(now);

        leenksToNotify.forEach(leenk -> {
            leenkNotificationUsecase.saveLeenkFinishedNotification(leenk);
            leenk.markAsFinishedNotified();
        });
        return leenksToNotify.size();
    }

    @Transactional
    public void notifyHostsOfUnclosedLeenks(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkGetService.findOverdueRecruitingLeenksToNotify(now);

        leenksToNotify.forEach(leenkNotificationUsecase::saveLeenkStartedHostReminderNotification);

    }
}
