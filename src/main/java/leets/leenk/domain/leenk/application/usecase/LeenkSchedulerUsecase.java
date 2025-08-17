package leets.leenk.domain.leenk.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.service.LeenkStatusBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LeenkSchedulerUsecase {

    private final LeenkStatusBatchService leenkStatusBatchService;

    @Transactional
    public List<Leenk> finishDueLeenks(LocalDateTime now) {
        List<Leenk> leenksToFinish = leenkStatusBatchService.finishDueLeenks(now);

        leenksToFinish.forEach(Leenk::changeStatusToFinished);
        return leenksToFinish;
    }
}
