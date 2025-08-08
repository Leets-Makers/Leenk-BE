package leets.leenk.domain.leenk.domain.service;

import java.util.List;
import leets.leenk.domain.leenk.application.exception.LeenkNotFoundException;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.leenk.domain.repository.LeenkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeenkGetService {

    private static final List<LeenkStatus> ALL_STATUSES = List.of(
            LeenkStatus.RECRUITING, LeenkStatus.CLOSED
    );
    private final LeenkRepository leenkRepository;

    public Leenk findById(Long leenkId) {
        return leenkRepository.findById(leenkId)
                .orElseThrow(LeenkNotFoundException::new);
    }

    public Slice<Leenk> findAll(Pageable pageable) {

        return leenkRepository.findAllByStatusIn(ALL_STATUSES, pageable);
    }

    public Slice<Leenk> findByStatus(LeenkStatus status, Pageable pageable) {

        return leenkRepository.findAllByStatus(status, pageable);
    }

    public Slice<Leenk> findByStatusParam(LeenkFilter filter, Pageable pageable) {
        if (filter == LeenkFilter.ALL) {
            return leenkRepository.findAllByStatusIn(ALL_STATUSES, pageable);
        }

        return leenkRepository.findAllByStatus(filter.getLeenkStatus(), pageable);
    }
}
