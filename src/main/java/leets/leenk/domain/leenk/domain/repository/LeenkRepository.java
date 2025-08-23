package leets.leenk.domain.leenk.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeenkRepository extends JpaRepository<Leenk, Long> {

    Slice<Leenk> findAllByStatus(LeenkStatus status, Pageable pageable);

    Slice<Leenk> findAllByStatusIn(List<LeenkStatus> statuses, Pageable pageable);

    List<Leenk> findAllByStatusInAndIsFinishedNotifiedIsFalseAndStartTimeLessThanEqual(List<LeenkStatus> statuses,
                                                                                       LocalDateTime startTime);

    List<Leenk> findAllByIsFinishedNotifiedIsFalseAndStatusAndStartTimeLessThanEqual(LeenkStatus status,
                                                                                     LocalDateTime startTime);

    List<Leenk> findAllByStatusAndStartTimeGreaterThanAndStartTimeLessThanEqual(LeenkStatus status, LocalDateTime startTimeAfter,
                                                   LocalDateTime startTimeBefore);

    List<Leenk> findAllByStatusInAndStartTimeGreaterThanAndStartTimeLessThanEqual(List<LeenkStatus> statuses,
                                                     LocalDateTime now, LocalDateTime startTime);
}
