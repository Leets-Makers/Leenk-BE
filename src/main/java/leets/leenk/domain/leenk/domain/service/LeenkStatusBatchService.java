package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeenkStatusBatchService {

    private final LeenkRepository leenkRepository;

    @Transactional
    public int finishDueLeenks(LocalDateTime now) {
        return leenkRepository.finishDue(now, LeenkStatus.FINISHED,
                List.of(LeenkStatus.RECRUITING, LeenkStatus.CLOSED));
    }
}
