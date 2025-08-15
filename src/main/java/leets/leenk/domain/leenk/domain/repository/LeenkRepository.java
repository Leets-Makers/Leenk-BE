package leets.leenk.domain.leenk.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeenkRepository extends JpaRepository<Leenk, Long> {

    Slice<Leenk> findAllByStatus(LeenkStatus status, Pageable pageable);

    Slice<Leenk> findAllByStatusIn(List<LeenkStatus> statuses, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Leenk leenk
               set leenk.status = :finished
             where leenk.status in :targets
               and leenk.startTime <= :now
            """)
    int finishDue(@Param("now") LocalDateTime now, @Param("finished") LeenkStatus finished,
                  @Param("targets") List<LeenkStatus> targets);

    List<Leenk> findAllByStatusInAndStartTimeGreaterThanAndStartTimeLessThanEqual(List<LeenkStatus> statuses,
                                                     LocalDateTime now, LocalDateTime startTime);
}
