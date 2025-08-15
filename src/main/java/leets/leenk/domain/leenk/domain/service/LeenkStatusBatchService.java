package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import leets.leenk.domain.notification.application.usecase.LeenkNotificationUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeenkStatusBatchService {

    private final LeenkRepository leenkRepository;

    private final LeenkNotificationUsecase leenkNotificationUsecase;

    @Transactional
    public int finishDueLeenks(LocalDateTime now) {
        return leenkRepository.finishDue(now, LeenkStatus.FINISHED,
                List.of(LeenkStatus.RECRUITING, LeenkStatus.CLOSED));
    }

    public void notifyStartSoon(LocalDateTime now) {
        List<Leenk> leenksToNotify = leenkRepository.findAllByStatusInAndStartTimeBetween(
                List.of(LeenkStatus.RECRUITING, LeenkStatus.CLOSED), now, now.plusMinutes(30));

        leenksToNotify.forEach(leenkNotificationUsecase::saveLeenkStartingSoonNotification);
    }
}
