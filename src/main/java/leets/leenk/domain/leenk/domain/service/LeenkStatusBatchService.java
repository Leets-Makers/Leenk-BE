package leets.leenk.domain.leenk.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkStatusBatchService {

    private final LeenkRepository leenkRepository;


    public List<Leenk> findDueLeenks(LocalDateTime now) {
        return leenkRepository.findAllByStatusInAndStartTimeLessThanEqual(List.of(LeenkStatus.RECRUITING, LeenkStatus.CLOSED), now);
    }

    public List<Leenk> findLeenksStartingSoon(LocalDateTime now) {
        return leenkRepository.findAllByStatusInAndStartTimeGreaterThanAndStartTimeLessThanEqual(
                List.of(LeenkStatus.RECRUITING, LeenkStatus.CLOSED), now, now.plusMinutes(30));
    }
}
